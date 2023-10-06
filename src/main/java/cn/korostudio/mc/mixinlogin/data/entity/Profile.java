package cn.korostudio.mc.mixinlogin.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pid;
    private String uuid;  //! 游戏的uuid
    @ManyToOne
    private UserData userdata; // 与User的多对一关系
    private String name;
    private Long timestamp;
    private String model_type;
    //? default / slim / ysm
    private Boolean uploadable_skin;
    private Boolean uploadable_cape;
    private String skin_url;
    private String cape_url;
    //? 原版部分
    private Boolean uploadable_community_model;
    private String community_model_name;
    //? YSM/CPM支持


    public void setUserData(UserData userdata) {
        this.userdata = userdata;
        if (!userdata.getProfiles().contains(this)) { // 防止无限循环
            userdata.getProfiles().add(this);
        }
    }
}
