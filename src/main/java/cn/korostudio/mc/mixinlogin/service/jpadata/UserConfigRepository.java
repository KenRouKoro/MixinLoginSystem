package cn.korostudio.mc.mixinlogin.service.jpadata;

import cn.korostudio.mc.mixinlogin.data.entity.UserConfig;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserConfigRepository extends JpaRepository<UserConfig,String> , JpaSpecificationExecutor<UserConfig> {

}
