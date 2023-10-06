package cn.korostudio.mc.mixinlogin.service.jpadata;

import cn.korostudio.mc.mixinlogin.data.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
