package cn.korostudio.mc.mixinlogin;

import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import cn.korostudio.mc.mixinlogin.service.user.UserLoadService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
@Theme(value = "mixinlogin")
public class MixinLoginApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MixinLoginApplication.class, args);
    }

}
