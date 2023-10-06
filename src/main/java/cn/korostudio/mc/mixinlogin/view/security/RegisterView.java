package cn.korostudio.mc.mixinlogin.view.security;

import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Register")
@Route(value = "registerview")
@CssImport("./styles/register-styles.css")
public class RegisterView extends Div {
    private final AuthenticationService authenticationService;

    public RegisterView(@Autowired AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
        addClassName("register-rich-content");
        VerticalLayout leftDiv = createLeftDiv();
        add(leftDiv);
    }

    private VerticalLayout createLeftDiv() {
        VerticalLayout leftDiv = new VerticalLayout();
        leftDiv.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        leftDiv.setHeightFull();
        leftDiv.setClassName("register-left-div");
        FormLayout formLayout = createFormLayout();
        leftDiv.add(formLayout);
        return leftDiv;
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        H1 title = new H1("Register");
        Paragraph subtitle = createSubtitle();
        EmailField emailField = createEmailField();
        TextField usernameField = createUsernameField();
        PasswordField passwordField = createPasswordField();
        PasswordField rePasswordField = createRePasswordField();
        Button submitButton = createSubmitButton(emailField, usernameField, passwordField, rePasswordField);
        Anchor toLogin = createLoginAnchor();

        formLayout.add(title, subtitle, emailField, usernameField, passwordField, rePasswordField, submitButton, toLogin);
        return formLayout;
    }

    private Paragraph createSubtitle() {
        Paragraph subtitle = new Paragraph("WTU Minecraft Server Mixin Login System");
        subtitle.getStyle().set("margin-bottom","1.25em");
        return subtitle;
    }

    private EmailField createEmailField() {
        EmailField emailField = new EmailField("Email");
        emailField.setPattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
        emailField.setRequired(true);
        return emailField;
    }

    private TextField createUsernameField() {
        TextField usernameField = new TextField("Username");
        usernameField.setRequired(true);
        return usernameField;
    }

    private PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$");
        passwordField.setRequired(true);
        return passwordField;
    }

    private PasswordField createRePasswordField() {
        PasswordField rePasswordField = new PasswordField("Repeat Password");
        rePasswordField.setRequired(true);
        return rePasswordField;
    }

    private Button createSubmitButton(EmailField emailField, TextField usernameField, PasswordField passwordField, PasswordField rePasswordField) {
        Button submitButton = new Button("Submit", e -> {
            if (!passwordField.getValue().equals(rePasswordField.getValue())) {
                showNotification("Two password mismatches.", NotificationVariant.LUMO_ERROR);
                e.getSource().setEnabled(true);
                return;
            }

            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String email = emailField.getValue();

            if(authenticationService.hasUserData(email)){
                showNotification("Email is registered.", NotificationVariant.LUMO_ERROR);
                e.getSource().setEnabled(true);
                return;
            }

            UserData user = UserData.builder()
                    .name(username)
                    .email(email)
                    .roles("USER")
                    .build();

            if (authenticationService.isUserTableEmpty()){
                user.setRoles("ADMIN");
                user.setEnable(true);
                Notification notification = showNotification("You are the default administrator user.", NotificationVariant.LUMO_SUCCESS);
            }

            authenticationService.register(user,password);
            Notification notification = showNotification("Registered Successfully , Just a moment.", NotificationVariant.LUMO_SUCCESS);
            notification.addOpenedChangeListener(event->{
                if(!event.isOpened()){
                    event.getSource().getUI().ifPresent(ui->{
                        ui.navigate(LoginView.class);
                    });
                }
            });
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setDisableOnClick(true);
        submitButton.getStyle().set("margin-top","24px");
        return submitButton;
    }

    private Anchor createLoginAnchor() {
        Anchor toLogin = new Anchor("login","Have Credentials?Login now");
        toLogin.getStyle().set("margin-top","24px").set("text-align","center");
        return toLogin;
    }

    private Notification showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message,5000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(variant);
        return notification;
    }
}