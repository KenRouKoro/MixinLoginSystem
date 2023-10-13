package cn.korostudio.mc.mixinlogin.data.entity;

import cn.hutool.json.JSONObject;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserData  {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    String uid ;
    String name;
    String email;
    String passwd;
    String encryption_type;
    String roles;
    boolean enable = false;
    String preferredLanguage = "zh_CN";
    String select_profiles;



    @OneToOne
    UserConfig userConfig;
    @OneToMany(targetEntity = Profile.class, mappedBy = "userdata")
    List<Profile> profiles;
    @OneToMany(targetEntity = Token.class, mappedBy = "userdata")
    List<Token> tokens;

    public Profile addProFile(Profile profile){
        this.profiles.add(profile);
        return profile;
    }
    public List<Token> addToken(Token token){
        this.tokens.add(token);
        return tokens;
    }
    public String getStandardFormatPassword(){
        return STR."{\{encryption_type}\{passwd}}";
    }

    public String toYggJsonStr(){
        return STR."""
                {"id":"\{uid}","properties":[{"name":"preferredLanguage","value":"\{preferredLanguage}"}]}
                """;
    }

}
