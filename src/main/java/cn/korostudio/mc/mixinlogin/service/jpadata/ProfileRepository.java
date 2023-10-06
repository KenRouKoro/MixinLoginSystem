package cn.korostudio.mc.mixinlogin.service.jpadata;

import cn.korostudio.mc.mixinlogin.data.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
}
