package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.hutool.core.util.URLUtil;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Route(value = "user" ,layout = MainView.class)
@PermitAll
public class UserArchivesView extends VerticalLayout implements HasDynamicTitle {

    private final AuthenticationService authenticationService;
    private final UserData user;

    public UserArchivesView(@Autowired AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
        Optional<UserDetails> userDetailsOptional = authenticationService.getAuthenticatedUser();
        user = userDetailsOptional.map(authenticationService::getUserData).orElse(null);


        setSpacing(false);

        Image avatar = new Image(STR."https://api.paugram.com/gravatar/?email=\{ URLUtil.encode(user.getEmail(), StandardCharsets.UTF_8) }&replace=retro",user.getEmail());
        //avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatar.setWidth("150px");
        avatar.setHeight("150px");
        avatar.getStyle().set("border-radius","50%").set("object-fit","cover");


        H2 username = new H2(user.getName());
        Paragraph uid = new Paragraph(STR."uid:\{user.getUid()}");
        Tooltip tooltip = Tooltip.forComponent(uid)
                .withText("Different from Minecraft UUID")
                .withPosition(Tooltip.TooltipPosition.TOP_START);
        Paragraph email = new Paragraph(user.getEmail());


        add(avatar,username,email,uid);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

    }

    @Override
    public String getPageTitle() {
        return user!=null?STR."\{user.getName()}'s Archives":"Error";
    }
}
