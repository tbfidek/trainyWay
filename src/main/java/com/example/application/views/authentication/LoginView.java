package com.example.application.views.authentication;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.example.application.data.service.EmailService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;


@CssImport("../frontend/styles/views/auth/login-view.css")
@PageTitle("Login")
@Route("")
public class LoginView extends Div {

    private final EmailService emailService;

    public LoginView(AuthService authService, EmailService emailService) {
        this.emailService = emailService;
        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            UI.getCurrent().getPage().executeJs("document.location = '/home';");
        } else {
            setId("login-view");
            var username = new TextField("username");
            var password = new PasswordField("password");

            Button login = new Button("Login", buttonClickEvent -> {
                try {
                    authService.authenticate(username.getValue(), password.getValue());
                    UI.getCurrent().navigate("home");
                } catch (AuthService.AuthException e) {
                    Notification.show("Wrong credentials");
                }
            });
            login.addClassNames(LumoUtility.Margin.Bottom.SMALL);

            Button signup = new Button("Signup", buttonClickEvent -> {
                UI.getCurrent().navigate("signup");
            });
            signup.addClassNames(LumoUtility.Margin.Bottom.SMALL);

            Button forgot = new Button("Forgot password?", buttonClickEvent -> setPopupLayout());

            HorizontalLayout buttons = new HorizontalLayout();
            buttons.add(login, signup);

            add(
                    new H1("Welcome"),
                    username,
                    password,
                    buttons,
                    forgot
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
                Notification.show("instructions will be sent to your email");
                Thread inputThread = new Thread(() -> emailService.sendTemporaryPasswordEmail(emailField.getValue()));
                inputThread.start();
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
