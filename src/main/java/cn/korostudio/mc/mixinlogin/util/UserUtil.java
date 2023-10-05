package cn.korostudio.mc.mixinlogin.util;

import cn.hutool.core.util.StrUtil;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUtil {
    public static UserDetails UserDataToUser(UserData userData){
        return  User.builder()
                .username(userData.getEmail())
                .password(STR."{\{userData.getEncryption_type()}}\{userData.getPasswd()}")
                .roles(StrUtil.split(userData.getRoles(),' ').toArray(new String[0]))
                .build();
    }
}
