package cn.korostudio.mc.mixinlogin.config;

import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import cn.korostudio.mc.mixinlogin.service.user.UserLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@Slf4j
public class UserBeanConfig {
    @Bean
    public UserDetailsService userDetailsService(UserDataRepository repository) {
        return new UserLoadService(repository);
    }
}
