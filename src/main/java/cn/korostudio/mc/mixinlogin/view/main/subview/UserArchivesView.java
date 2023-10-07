package cn.korostudio.mc.mixinlogin.view.main.subview;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.korostudio.mc.mixinlogin.data.entity.UserData;
import cn.korostudio.mc.mixinlogin.service.security.AuthenticationService;
import cn.korostudio.mc.mixinlogin.view.main.MainView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Route(value = "user" ,layout = MainView.class)
@PermitAll
public class UserArchivesView extends VerticalLayout implements HasDynamicTitle , HasUrlParameter<String> {

    private final AuthenticationService authenticationService;
    private UserData user;
    private boolean isMine = false;

    private UserDataEditDialog dialog;

    public UserArchivesView(@Autowired AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
        Optional<UserDetails> userDetailsOptional = authenticationService.getAuthenticatedUser();
        user = userDetailsOptional.map(authenticationService::getUserData).orElse(null);
    }

    private void creatView(){
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

        email.addClassName(LumoUtility.Margin.NONE);
        uid.addClassName(LumoUtility.Margin.NONE);

        Paragraph info = new Paragraph(user.getUserConfig().getInfo());
        info.setWidth("400px");
        info.setClassName(LumoUtility.TextAlignment.LEFT);

        add(avatar,username,email,uid , info);

        if(isMine){
            dialog = new UserDataEditDialog(user,authenticationService);
            Button button = new Button("Edit Archives",e->{dialog.open();});
            add(button,dialog);
        }


        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Override
    public String getPageTitle() {
        return user!=null?STR."\{user.getName()}'s Archives":"Error";
    }

    @Override
    public void setParameter(BeforeEvent event,@OptionalParameter String parameter) {
        if(user==null)return;
        if (StrUtil.isBlankIfStr(parameter)){
            isMine = true;
        }else{
            Optional<UserData> userData = authenticationService.getUserDataByID(parameter);
            if (userData.isEmpty()){
                isMine = true;
            }else{
                if (user.getUid().equals(userData.get().getUid())){
                    isMine = true;
                }else{
                    user = userData.get();
                }
            }
        }
        creatView();
    }
    protected static class UserDataEditDialog extends Dialog{
        UserData userData;
        AuthenticationService service;

        TextField avatarTextField;
        TextField nameTextField;
        TextArea infoTextArea;


        public UserDataEditDialog(UserData userData , AuthenticationService service){
            this.userData = userData;
            this.service = service;
            setDialog();
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
            update();
        }
        private void update(){
            avatarTextField.setValue(userData.getUserConfig().getAvatarUrl());
            nameTextField.setValue(userData.getName());
            infoTextArea.setValue(userData.getUserConfig().getInfo());
        }

        private void setDialog(){
            setHeaderTitle(STR."Editing \{userData.getName()}'s Archives");

            Button cancel = new Button("Cancel",e->{close();});
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button save = new Button("Save",e->{saveUserData();close();});
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            getFooter().add(cancel,save);

            avatarTextField = new TextField("Avatar");
            avatarTextField.setPattern("https://[^/]+.*");
            avatarTextField.setMaxLength(200);
            avatarTextField.setWidthFull();

            nameTextField = new TextField("Name");
            nameTextField.setRequired(true);
            nameTextField.setPattern("^[A-Za-z\\-\\p{InHiragana}\\p{InKatakana}\\p{IsHan}]{2,16}$");
            nameTextField.setMaxLength(16);
            nameTextField.setMinLength(2);
            nameTextField.setWidthFull();

            infoTextArea = new TextArea("Info");
            infoTextArea.setMaxLength(200);
            infoTextArea.setWidthFull();

            add(avatarTextField,nameTextField,infoTextArea);

        }

        @Override
        public void open() {
            update();
            super.open();
        }

        private void saveUserData(){
            if (!avatarTextField.isInvalid()|| StrUtil.isBlankIfStr(avatarTextField.getValue())){
                userData.getUserConfig().setAvatarUrl("");
                userData.getUserConfig().setUseUrlAvatar(false);
            }else{
                userData.getUserConfig().setAvatarUrl(avatarTextField.getValue());
                userData.getUserConfig().setUseUrlAvatar(true);
            }
            if(nameTextField.isInvalid()){
                userData.setName(nameTextField.getValue());
            }
            if(infoTextArea.isInvalid()){
                userData.getUserConfig().setInfo(infoTextArea.getValue());
            }

            service.getDataRepository().save(userData);
            showNotification("Save Success",NotificationVariant.LUMO_SUCCESS);
        }
        private Notification showNotification(String message, NotificationVariant variant) {
            Notification notification = Notification.show(message,5000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(variant);
            return notification;
        }
    }
    private Notification showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message,5000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(variant);
        return notification;
    }
}
