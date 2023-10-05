package cn.korostudio.mc.mixinlogin.view.main;

import cn.hutool.core.util.URLUtil;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import cn.korostudio.mc.mixinlogin.view.main.subview.IndexView;
import cn.korostudio.mc.mixinlogin.view.main.subview.UserArchivesView;
import cn.korostudio.mc.mixinlogin.view.security.LoginView;
import cn.korostudio.mc.mixinlogin.view.security.RegisterView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@AnonymousAllowed
@PageTitle("Index")
public class MainView extends AppLayout {
    private final H2 viewTitle = new H2();
    private final AuthenticationService authenticationService;
    private final AccessAnnotationChecker accessChecker;

    public MainView(@Autowired AuthenticationService authenticationService, @Autowired AccessAnnotationChecker accessChecker) {
        this.authenticationService = authenticationService;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        addToNavbar(true, createDrawerToggle(), viewTitle);
    }

    private DrawerToggle createDrawerToggle() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        return toggle;
    }

    private void addDrawerContent() {
        addToDrawer(createHeader(), createScroller(), createFooter());
    }

    private Header createHeader() {
        H1 appName = new H1("WTU MixinLogin");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        return new Header(appName);
    }

    private Scroller createScroller() {
        return new Scroller(createNavigation());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        addItemIfAccess(nav, "Index", IndexView.class, LineAwesomeIcon.HOME_SOLID.create());
        addItemIfAccess(nav, "User Archives", UserArchivesView.class, LineAwesomeIcon.USER.create());
        return nav;
    }

    private  void addItemIfAccess(SideNav nav, String name, Class<?extends Component> viewClass, Component icon) {
        if (accessChecker.hasAccess(viewClass)) {
            nav.addItem(new SideNavItem(name, viewClass, icon));
        }
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<UserDetails> maybeUser = authenticationService.getAuthenticatedUser();
        if (maybeUser.isPresent()) {
            layout.getStyle().set("justify-content", "center");
            layout.add(createUserMenu(maybeUser.get()));
        } else {
            layout.getStyle().set("justify-content", "center");
            layout.add(createSignupButton(), createLoginButton());
        }

        return layout;
    }

    private MenuBar createUserMenu(UserDetails user) {
        UserData userData = authenticationService.getUserData(user);
        MenuBar userMenu = new MenuBar();
        userMenu.setThemeName("tertiary-inline contrast");
        userMenu.setWidthFull();

        MenuItem userName = userMenu.addItem("");
        Div div = createUserDiv(userData);
        userName.add(div);
        userName.getSubMenu().addItem("Sign out", e -> authenticationService.logout());

        return userMenu;
    }

    private Div createUserDiv(UserData userData) {
        Div div = new Div();
        div.add(createAvatar(userData));
        div.add(userData.getName());
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        div.setWidthFull();
        return div;
    }

    private Avatar createAvatar(UserData userData) {
        Avatar avatar = new Avatar(userData.getName());
        avatar.setImage(STR."https://api.paugram.com/gravatar/?email=\{URLUtil.encode(userData.getEmail(), StandardCharsets.UTF_8)}&replace=retro");
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");
        return avatar;
    }

    private Button createLoginButton() {
        Button loginLink = new Button("Sign in");
        loginLink.addClickListener(e -> navigateToView(e, LoginView.class));
        return loginLink;
    }

    private Button createSignupButton() {
        Button signupLink = new Button("Sign up");
        signupLink.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signupLink.addClickListener(e -> navigateToView(e, RegisterView.class));
        return signupLink;
    }

    private void navigateToView(ClickEvent<Button> e, Class<? extends  Component> viewClass) {
        Optional<UI> uiOptional = e.getSource().getUI();
        if (uiOptional.isPresent()) {
            UI ui = uiOptional.get();
            ui.navigate(viewClass);
        }
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        if (title!=null){
            return title.value();
        }
        if (getContent() instanceof HasDynamicTitle pageTitle){
            return pageTitle.getPageTitle();
        }
        return "";
    }
}
