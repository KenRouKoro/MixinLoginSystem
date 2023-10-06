package cn.korostudio.mc.mixinlogin.data.entity;

import cn.hutool.bloomfilter.filter.SDBMFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserConfig {
    @Id
    String uid;

    @Lob
    String info = "";
    @Lob
    String extra = "";

    String avatarUrl = "";

    boolean useUrlAvatar = false;

    @MapsId
    @JoinColumn(name = "uid")
    @OneToOne
    UserData userData;
}
