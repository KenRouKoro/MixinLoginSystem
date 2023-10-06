package cn.korostudio.mc.mixinlogin;

import cn.korostudio.mc.mixinlogin.config.ApplicationConfig;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import cn.korostudio.mc.mixinlogin.service.user.UserLoadService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

@SpringBootApplication
@Theme(value = "mixinlogin")
public class MixinLoginApplication implements AppShellConfigurator {

    public static void main(String[] args) {

        SpringApplication.run(MixinLoginApplication.class, args);
    }

    @Bean
    public TypeExcludeFilter typeExcludeFilter() {
        return new InterceptTypeExcludeFilter();
    }
    public static class InterceptTypeExcludeFilter extends TypeExcludeFilter{
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            return (!ApplicationConfig.CONFIG.isEnableWebView()) && metadataReader.getClassMetadata().getClassName().startsWith("cn.korostudio.mc.mixinlogin.view");
        }
        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return getClass().equals(obj.getClass());
        }
    }

}
