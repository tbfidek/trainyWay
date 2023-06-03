package com.example.application.views.authentication;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Route("resetpassword/:resetCode")
public class ResetPasswordView extends Div implements BeforeEnterObserver {
    private String resetCode;
    private PasswordField passwordField;
    private PasswordField passwordConfirmField;
    private UserRepository userRepository;

    public ResetPasswordView(UserRepository userRepository) {
        Dialog resetDialog = new Dialog();
        this.userRepository = userRepository;
        if (VaadinSession.getCurrent().getAttribute(User.class) == null) {
            addAttachListener(attachEvent -> {
                if(userRepository.checkResetCode(resetCode) == 0){
                    UI.getCurrent().navigate(" ");
                    Notification n = Notification.show("Invalid code!");
                    n.setDuration(4000);
                    n.setPosition(Notification.Position.BOTTOM_START);
                    n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                } else {
                    Button cancelButton = new Button("Cancel", e -> {
                        resetDialog.close();
                        UI.getCurrent().navigate(" ");
                    });
                    Button savePasswordButton = new Button("Reset", e -> {

                        if(!passwordField.getValue().equals(passwordConfirmField.getValue())){
                            passwordConfirmField.setHelperText("Password nu sunt la fel");
                        } else {
                            User u = userRepository.getByEmail(userRepository.getEmailByCode(resetCode));

                            String passwordSalt = RandomStringUtils.randomAlphabetic(32);
                            String passwordHash = DigestUtils.sha1Hex(passwordField.getValue() + passwordSalt);
                            u.setPasswordSalt(passwordSalt);
                            u.setPasswordHash(passwordHash);
                            userRepository.save(u);
                            Notification n = Notification.show("Your password was reset!");
                            n.setDuration(4000);
                            n.setPosition(Notification.Position.BOTTOM_START);
                            n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                            UI.getCurrent().navigate(" ");
                            userRepository.deleteResetQuery(resetCode);
                        }
                    });
                    resetDialog.add(createDialogLayout());
                    resetDialog.getFooter().add(cancelButton);
                    resetDialog.getFooter().add(savePasswordButton);
                    add(resetDialog);

                    resetDialog.open();
                }
            });

        } else {
            UI.getCurrent().getPage().executeJs("document.location = '/';");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        resetCode = beforeEnterEvent.getRouteParameters().get("resetCode").get();
    }

    private VerticalLayout createDialogLayout() {

        passwordField = new PasswordField("Please enter new password:");
        passwordConfirmField = new PasswordField("Please confirm new password:");
        VerticalLayout dialogLayout = new VerticalLayout(passwordField, passwordConfirmField);

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}
