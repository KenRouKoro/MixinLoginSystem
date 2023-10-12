package cn.korostudio.mc.mixinlogin.api.controller.auth;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/authserver")
@Slf4j
public class AuthController {


    @Autowired
    private AuthenticationContext authenticationContext;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
        String clientToken;
        try{

            clientToken = (String) params.get("clientToken");
        }catch (Exception exception){
            clientToken = YggdrasUUID.generateNormalUUID();
        }

        //? 根据邮件查找用户
        List<Object[]> passwords = dataRepository.findPasswdByEmail(email);
        UserData userData = dataRepository.findByEmail(email);
        if(
            userData == null ||
            password.isEmpty() ||
            passwords.isEmpty()
        ){
            return String.valueOf(HttpStatus.HTTP_FORBIDDEN);
            //! 似乎不合規範 遲點再處理
        }

        for(Object[] objs:passwords){
            if (! passwordEncoder.matches(password,STR."{\{objs[0]}}\{objs[1]}")){
                return String.valueOf(HttpStatus.HTTP_FORBIDDEN);
                //! 似乎不合規範 遲點再處理
            }
        }
        //! 验证通过
        JSONObject returnJson = new JSONObject();
        Token token =Token.getNewToken(clientToken);
        userData.addToken(token);
        dataRepository.save(userData);
        //? 配置文件/角色列表
        List<Profile> Profiles = userData.getProfiles();



        return params.toString();
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
}