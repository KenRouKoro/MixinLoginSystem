package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "index", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@AnonymousAllowed
@PageTitle("Index")
public class IndexView extends VerticalLayout {
    public IndexView(){

        setSpacing(false);

        Image img = new Image("public/images/90723001.png", "Pixiv ID:90723001");
        img.setWidth("200px");
        img.setHeight("200px");
        img.getStyle().set("border-radius","50%").set("object-fit","cover");
        add(img);

        H2 header = new H2("WTU Minecraft Server Mixin Login System");
        header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("Official WUHAN TEXTILE UNIVERSITY Minecraft Server"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}
