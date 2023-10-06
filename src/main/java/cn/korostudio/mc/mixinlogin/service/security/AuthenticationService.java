package cn.korostudio.mc.mixinlogin.service.security;

import cn.korostudio.mc.mixinlogin.data.entity.UserConfig;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserConfigRepository;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthenticationService {

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

    public static final String RESTR = "\\{(.+?)\\}(.*)";

    /**
     * 使用用户名密码在当前上下文进行登录
     * @param username 用户名（邮箱）
     * @param password 密码（未编码）
     */
    public void login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 使用用户名密码验证用户，不进行登录
     * @param username 用户名（邮箱）
     * @param password 密码（未编码）
     * @return 验证是否通过
     */
    public boolean verification(String username, String password){
        List<Object[]> passwords = dataRepository.findPasswdByEmail(username);
        if (passwords.isEmpty())return false;
        for(Object[] objs:passwords){
            if (passwordEncoder.matches(password,STR."{\{objs[0]}}\{objs[1]}"))return true;
        }
        return false;
    }
    @Transactional
    public UserConfig createUserConfig(UserData userData){
        return UserConfig.builder()
                .useUrlAvatar(false)
                .avatarUrl("")
                .info("")
                .userData(userData)
                .build();
    }

    /**
     * 这个方法不应由外部调用
     * @param userData
     */
    @Transactional
    public void register(UserData userData){
        log.info(userData.toString());
        userData.setUserConfig(createUserConfig(userData));
        dataRepository.save(userData);
        configRepository.save(userData.getUserConfig());
    }

    /**
     * 注册方法，会自动构建伴生的UserConfig，并对密码编码，但不会登录当前上下文
     * @param userData 构建好的UserData类型
     * @param unencryptedPasswd 未编码的原始密码
     */
    @Transactional
    public void register(UserData userData,String unencryptedPasswd){
        String encryptedPasswd = passwordEncoder.encode(unencryptedPasswd);
        Pattern pattern = Pattern.compile(RESTR);
        Matcher matcher = pattern.matcher(encryptedPasswd);
        if(!matcher.find()){
            return;
        }

        userData.setEncryption_type(matcher.group(1));
        userData.setPasswd(matcher.group(2));
        register(userData);
    }
    @Transactional
    public UserData getUserData(UserDetails userDetails){
        UserData userData = dataRepository.findByEmail(userDetails.getUsername());
        if (userData==null)throw  new UsernameNotFoundException(STR."not UserData define \{userDetails.getUsername()}");
        return userData;
    }
    @Transactional
    public boolean isUserTableEmpty() {
        return dataRepository.count() == 0;
    }
    @Transactional
    public boolean hasUserData(String email){
        return dataRepository.existsByEmail(email);
    }

    public Optional<UserDetails> getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class);
    }

    @Transactional
    public Optional<UserConfig> getUserConfigByEmail(String email){
        String id = dataRepository.findUidByEmail(email);
        if (id==null){
            return Optional.empty();
        }
        return configRepository.findById(id);
    }

    /**
     * 在当前上下文中登出
     */
    public void logout() {
        authenticationContext.logout();
    }
}