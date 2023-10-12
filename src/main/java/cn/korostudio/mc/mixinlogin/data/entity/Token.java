package cn.korostudio.mc.mixinlogin.data.entity;


import cn.korostudio.mc.mixinlogin.util.YggdrasUUID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String tid;
    @ManyToOne
    UserData userdata;  // 与User的多对一关系
    String access_token;
    String client_token;
    String selectedProfile_pid;
    //? 令牌状态 valid / temporarily_invalid / invalid
    String stage;
    //? 创建时的时间戳
    long timestamp_of_creation;
    @OneToOne
    Profile profile;

    public void setUsers(UserData userdata) {
        this.userdata = userdata;
        if (!userdata.getTokens().contains(this)) { // 防止无限循环
            userdata.getTokens().add(this);
        }
    }
    static public Token getNewToken(String clientToken){
        String accessToken = YggdrasUUID.generateNormalUUID();
        Token newToken = new Token();
        newToken.setAccess_token(accessToken);
        newToken.setClient_token(clientToken);
        newToken.setSelectedProfile_pid(null);
        newToken.setStage("valid"); //? 设置令牌有效
        newToken.setTimestamp_of_creation(System.currentTimeMillis());
        return newToken;
    }
}
