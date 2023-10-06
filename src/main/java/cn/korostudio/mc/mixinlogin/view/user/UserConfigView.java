package cn.korostudio.mc.mixinlogin.view.user;

import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "user" ,layout = MainView.class)
@PermitAll
public class UserConfigView {
}
