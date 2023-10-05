package cn.korostudio.mc.mixinlogin.view.security;

import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Optional;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticationService authenticatedUser;

    public LoginView(AuthenticationService authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Welcome");
        i18n.getHeader().setDescription("WTU Minecraft Server Mixin Login System");
        i18n.getForm().setForgotPassword("No credentials? Sign up now!");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        addForgotPasswordListener(event->{
            Optional<UI> uiOptional = getUI();
            if (uiOptional.isEmpty())return;
            UI ui = uiOptional.get();
            ui.navigate(RegisterView.class);
        });

        setForgotPasswordButtonVisible(true);


        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.getAuthenticatedUser().isPresent()){
            // Already logged in
            setOpened(false);
            event.getUI().navigate("/index");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}