package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin" ,layout = MainView.class)
@RolesAllowed("ADMIN")
public class AdminCenterView extends Div {
    public AdminCenterView(){
        add(new H1("Hello Admin!"));
    }
}
