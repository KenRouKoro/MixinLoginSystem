package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "rolemanagement" ,layout = MainView.class)
@PermitAll
public class RoleManagementView extends Div {
    public RoleManagementView(){
        add(new H1("Hello User/Admin!"));
    }

}
