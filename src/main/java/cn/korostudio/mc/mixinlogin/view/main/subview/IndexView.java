package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "index", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@AnonymousAllowed
@PageTitle("Index")
public class IndexView extends HorizontalLayout {
    public IndexView(){

        add(new H1("Hello World!"));

    }
}
