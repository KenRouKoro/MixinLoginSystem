package cn.korostudio.mc.mixinlogin;

import cn.korostudio.mc.mixinlogin.config.ApplicationConfig;
import cn.korostudio.mc.mixinlogin.service.jpadata.UserDataRepository;
import cn.korostudio.mc.mixinlogin.service.user.UserLoadService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@SpringBootApplication
@Theme(value = "mixinlogin")
public class MixinLoginApplication implements AppShellConfigurator {

    private static final String PUBLIC_KEY_FILE = "publicKey";
    private static final String PRIVATE_KEY_FILE = "privateKey";
    @Getter
    private static PublicKey publicKey = null;
    @Getter
    private static PrivateKey privateKey = null;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        loadRSA();
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

    public static void loadRSA() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        if (!Files.exists(Paths.get(PUBLIC_KEY_FILE)) || !Files.exists(Paths.get(PRIVATE_KEY_FILE))) {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(4096);
            KeyPair pair = keyGen.generateKeyPair();
            saveToFile(PUBLIC_KEY_FILE, pair.getPublic().getEncoded());
            saveToFile(PRIVATE_KEY_FILE, pair.getPrivate().getEncoded());
        }

        // Load keys from files
        byte[] publicBytes = Files.readAllBytes(Paths.get(PUBLIC_KEY_FILE));
        byte[] privateBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        publicKey = kf.generatePublic(new X509EncodedKeySpec(publicBytes));
        privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
    }

    public static void saveToFile(String fileName, byte[] key) throws IOException {
        File f = new File(STR."\{System.getProperty("user.dir")}/\{fileName}.rsa");
        f.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(key);
        }
    }

}
