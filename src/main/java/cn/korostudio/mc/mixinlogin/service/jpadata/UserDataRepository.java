package cn.korostudio.mc.mixinlogin.service.jpadata;

import cn.korostudio.mc.mixinlogin.data.entity.UserConfig;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDataRepository extends JpaRepository<UserData,String> , JpaSpecificationExecutor<UserData> {
    UserData findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("select u.uid from UserData u where u.email = ?1")
    String findUidByEmail(String email);
    @Query("select u.userConfig from UserData u where u.email = ?1")
    UserConfig findUserConfigByEmail(String email);
    @Query("select u.userConfig from UserData u where u.uid = ?1")
    UserConfig findUserConfigByUID(String UID);
    @Query("select u.encryption_type, u.passwd from UserData u where u.email = ?1")
    List<Object[]> findPasswdByEmail(String email);

}
