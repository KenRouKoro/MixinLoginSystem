package cn.korostudio.mc.mixinlogin.data.entity;


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
    private String tid;
    @ManyToOne
    private UserData userdata;  // 与User的多对一关系
    private String access_token;
    private String client_token;
    @OneToOne
    private Profile profile;

    public void setUsers(UserData userdata) {
        this.userdata = userdata;
        if (!userdata.getTokens().contains(this)) { // 防止无限循环
            userdata.getTokens().add(this);
        }
    }
}
