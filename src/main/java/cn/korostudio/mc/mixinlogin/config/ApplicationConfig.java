package cn.korostudio.mc.mixinlogin.config;

import cn.korostudio.mc.mixinlogin.util.TOMLConfigUtil;
import lombok.Data;
import lombok.Getter;

@Data
public class ApplicationConfig {

    public final static ApplicationConfig CONFIG;
    static {
        CONFIG = TOMLConfigUtil.getInstance("Config",ApplicationConfig.class);
    }
     boolean EnableWebView = true;
}
