package cn.korostudio.mc.mixinlogin.api.controller.auth;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.korostudio.mc.mixinlogin.config.ApplicationConfig;
import cn.korostudio.mc.mixinlogin.data.bean.YggError;
import cn.korostudio.mc.mixinlogin.data.entity.Profile;
import cn.korostudio.mc.mixinlogin.data.entity.Token;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.jpadata.TokenRepository;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserConfigRepository;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import cn.korostudio.mc.mixinlogin.util.YggdrasUUID;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/authserver")
@Slf4j
public class AuthController {

    //速率限制
    private static final int MAX_REQUESTS = ApplicationConfig.CONFIG.getMaxRequest(); // 限制在一段时间内的最大请求次数
    private static final int TIME_WINDOW = ApplicationConfig.CONFIG.getTimeWindow(); // 时间窗口，单位为秒
    private static final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static {
        // 每隔TIME_WINDOW秒清除计数器
        scheduler.scheduleAtFixedRate(() -> requestCounts.clear(), TIME_WINDOW, TIME_WINDOW, TimeUnit.SECONDS);
    }

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserDataRepository dataRepository;
    @Autowired
    private UserConfigRepository configRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @PostMapping(value = "/authenticate", produces = "application/json;charset=UTF-8")
    public String authenticate(@RequestBody JSONObject params, HttpServletRequest request) {
        //! 实现邮箱登录
        String email = (String) params.get("username");
        String password = (String) params.get("password");
        Boolean requestUser = (Boolean) params.get("requestUser");
        //请求速率限制
        if(!isAllow(email)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, YggError.InvalidCredentials("User authentication reaches rate limit").toJsonStr());
        }

        String clientToken;
        try{
            clientToken = (String) params.get("clientToken");
        }catch (Exception exception){
            clientToken = YggdrasUUID.generateNormalUUID();
        }

        //? 根据邮件查找用户
        //List<Object[]> passwords = dataRepository.findPasswdByEmail(email);
        //UserData userData = dataRepository.findByEmail(email);
        Optional<UserData>userDataOptional = authenticationService.findUserDataByEmail(email);

        if(
                userDataOptional.isEmpty() ||
                password.isEmpty()
        ){
            //return String.valueOf(HttpStatus.HTTP_FORBIDDEN);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, YggError.InvalidCredentials("Non-existent users").toJsonStr());
            //! 似乎不合規範 遲點再處理，finish
        }
        /*
        for(Object[] objs:passwords){

            if (! passwordEncoder.matches(password,STR."{\{objs[0]}}\{objs[1]}")){
                return String.valueOf(HttpStatus.HTTP_FORBIDDEN);
                //! 似乎不合規範 遲點再處理

        }
         */
        UserData userData = userDataOptional.get();
        if(!authenticationService.verification(userData.getEmail(),userData.getStandardFormatPassword())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,YggError.InvalidCredentials("Password error").toJsonStr());
        }

        List<Profile> profiles = userData.getProfiles();
        if (profiles.isEmpty()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,YggError.builder().error("NoProfileException").errorMessage("No Profile was created").cause("The account does not create any Profile").build().toJsonStr());
        }

        //! 验证通过
        JSONObject returnJson = new JSONObject();
        Token token =Token.getNewToken(clientToken);
        userData.addToken(token);

        //? 配置文件/角色列表


        returnJson.putOnce("accessToken",token.getAccess_token());
        returnJson.putOnce("clientToken",token.getClient_token());
        returnJson.putOnce("availableProfiles",profiles);
        if (userData.getSelect_profiles()==null){
            userData.setSelect_profiles(profiles.get(0).getPid());
        }
        Profile selectProfile = null;
        for(Profile profile:profiles){
            if (profile.getPid().equals(userData.getSelect_profiles())){
                selectProfile = profile;
                break;
            }
        }
        if (selectProfile == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,YggError.builder().error("NoProfileException").errorMessage("No Profile was created").cause("The account does not select any Profile").build().toJsonStr());
        }
        returnJson.putOnce("selectedProfile",List.of(new Profile[]{selectProfile}));

        if(requestUser){
            returnJson.putOnce("user",JSONUtil.parseObj(userData.toYggJsonStr()));
        }

        dataRepository.save(userData);

        return returnJson.toString();
    }

    @PostMapping(value = "/refresh", produces = "application/json;charset=UTF-8")
    public String refresh(@RequestBody JSONObject params, HttpServletRequest request) {
        return params.toString();
    }

    @PostMapping(value = "/validate", produces = "application/json;charset=UTF-8")
    public String validate(@RequestBody JSONObject params, HttpServletRequest request) {
        return params.toString();
    }

    @PostMapping(value = "/invalidate", produces = "application/json;charset=UTF-8")
    public String invalidate(@RequestBody JSONObject params, HttpServletRequest request) {
        return params.toString();
    }

    @PostMapping(value = "/signout" )
    public String signout(@RequestBody JSONObject params, HttpServletRequest request) {
        return params.toString();
    }
    //=======================速率限制方法===========================

    public static boolean isAllow(String user) {

        // 获取当前用户的请求次数
        Integer count = requestCounts.get(user);

        if (count == null) {
            // 如果用户还没有任何请求，就将其请求次数设为1
            requestCounts.put(user, 1);
            return true;
        } else if (count < MAX_REQUESTS) {
            // 如果用户的请求次数小于最大请求次数，就增加其请求次数
            requestCounts.put(user, count + 1);
            return true;
        } else {
            // 如果用户的请求次数已经达到最大请求次数，就拒绝请求
            return false;
        }
    }
}