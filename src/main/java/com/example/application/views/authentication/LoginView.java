package com.example.application.views.authentication;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.example.application.data.service.EmailService;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

@CssImport("../frontend/styles/views/auth/login-view.css")
@PageTitle("trainyWay | login")
@Route("")
public class LoginView extends Div {
    private final EmailService emailService;
    private final UserRepository userRepository;

    public LoginView(AuthService authService, EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            UI.getCurrent().getPage().executeJs("document.location = '/home';");
        } else {
            setId("login-view");
            var username = new TextField("username");
            var password = new PasswordField("password");

            Button login = new Button("login", buttonClickEvent -> {
                try {
                    authService.authenticate(username.getValue(), password.getValue());
                    UI.getCurrent().navigate("home");
                } catch (AuthService.AuthException e) {
                    Notification n = Notification.show("wrong credentials");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    n.setDuration(1000);
                }
            });
            login.addClassNames(LumoUtility.Margin.Bottom.SMALL);

            Button signup = new Button("signup", buttonClickEvent -> UI.getCurrent().navigate("signup"));
            signup.addClassNames(LumoUtility.Margin.Bottom.SMALL);

            Button forgot = new Button("forgot password?", buttonClickEvent -> setPopupLayout());
            forgot.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.SMALL);
            add(
                    new H1("Welcome"),
                    username,
                    password,
                    forgot,
                    login,
                    signup
            );
        }
    }

    private void setPopupLayout() {
        Dialog dialog = new Dialog();
        EmailField emailField = new EmailField();
        emailField.setLabel("enter an email address:");
        emailField.getElement().setAttribute("name", "email");
        emailField.setClearButtonVisible(true);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button sendButton = new Button("Send", buttonClickEvent1 -> {
            if(!emailField.isInvalid()) {

                User u = userRepository.getByEmail(emailField.getValue().trim());
                Notification n;
                if(u == null){
                    n = Notification.show("no user found with this email");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    n.setDuration(1000);
                } else {
                    n = Notification.show("instructions will be sent to your email");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    Thread inputThread = new Thread(() -> emailService.sendTemporaryPasswordEmail(emailField.getValue().trim()));
                    inputThread.start();
                }
                dialog.close();
            }
            else {
                emailField.setErrorMessage("invalid email format");
            }
        });
        dialog.getFooter().add(sendButton);
        dialog.getFooter().add(cancelButton);
        dialog.getHeader().add(emailField);
        add(dialog);
        dialog.open();
    }

}
