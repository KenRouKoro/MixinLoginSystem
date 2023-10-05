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


    public void login(String username, String password) {
        log.info(STR."username:\{username} , password:\{password}");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
    @Transactional
    public void register(UserData userData){
        log.info(userData.toString());
        userData.setUserConfig(createUserConfig(userData));
        dataRepository.save(userData);
        configRepository.save(userData.getUserConfig());
    }
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

    public void logout() {
        authenticationContext.logout();
    }
}