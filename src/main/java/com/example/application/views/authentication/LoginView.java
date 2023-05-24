package com.example.application.views.authentication;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.example.application.data.service.EmailService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


@CssImport("../frontend/styles/views/auth/login-view.css")
@PageTitle("Login")
@Route("")
public class LoginView extends Div {

    private EmailService emailService;
    public LoginView(AuthService authService, EmailService emailService) {
        this.emailService = emailService;
        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            UI.getCurrent().getPage().executeJs("document.location = '/home';");
        } else {
            setId("login-view");
            var username = new TextField("username");
            var password = new PasswordField("password");
            add(
                    //        save.addClickShortcut(Key.ENTER);

                    new H1("Welcomee"),
                    username,
                    password,
                    new Button("Login", buttonClickEvent -> {
                        try {
                            authService.authenticate(username.getValue(), password.getValue());
                            UI.getCurrent().navigate("home");
                        } catch (AuthService.AuthException e) {
                            Notification.show("Wrong credentials");
                        }
                    }),
                    new Button("Signup", buttonClickEvent -> {
                            //authService.authenticate(username.getValue(), password.getValue());
                            UI.getCurrent().navigate("signup");

                    }),
                    new Button("Forgot password?", buttonClickEvent -> {
                        Notification.show("instructions will be sent to your email");
                        Thread inputThread = new Thread(() -> {
                            emailService.sendTemporaryPasswordEmail("impeste@gmail.com");
                        });
                        inputThread.start();
                    })
            );

        }
    }

}
