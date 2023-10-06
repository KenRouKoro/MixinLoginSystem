package cn.korostudio.mc.mixinlogin.data.entity;

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
    private String preferredLanguage;

    @OneToOne
    UserConfig userConfig;
    @OneToMany(targetEntity = Profile.class, mappedBy = "users")
    List<Profile> profiles;
    @OneToMany(targetEntity = Token.class, mappedBy = "users")
    List<Token> tokens;

    public Profile addProFile(Profile profile){
        this.profiles.add(profile);
        return profile;
    }
    public Token addToken(Token tokens){
        this.tokens.add(tokens);
        return tokens;
    }

}
