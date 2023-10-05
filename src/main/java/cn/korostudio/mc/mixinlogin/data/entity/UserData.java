package cn.korostudio.mc.mixinlogin.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

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
    @OneToOne
    UserConfig userConfig;

}
