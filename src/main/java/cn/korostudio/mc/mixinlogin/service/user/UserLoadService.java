package cn.korostudio.mc.mixinlogin.service.user;

import cn.hutool.core.util.StrUtil;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
public class UserLoadService implements UserDetailsService {

    private final UserDataRepository repository;

    public UserLoadService(UserDataRepository repository){
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserData> userDataOptional = repository.findByEmail(username);
        if (userDataOptional.isEmpty()){
            throw  new UsernameNotFoundException(STR."not UserData define \{username}");
        }
        UserData userData = userDataOptional.get();
        return User.builder()
                .username(userData.getEmail())
                .password(STR."{\{userData.getEncryption_type()}}\{userData.getPasswd()}")
                .roles(StrUtil.split(userData.getRoles(),' ').toArray(new String[0]))
                .build();
    }
}
