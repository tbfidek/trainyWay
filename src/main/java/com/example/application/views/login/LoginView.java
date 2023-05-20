package com.example.application.views.login;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
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

    public LoginView(AuthService authService) {
        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            UI.getCurrent().getPage().executeJs("document.location = '/home';");
        } else {
            setId("login-view");
            var username = new TextField("username");
            var password = new PasswordField("password");
            add(
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

                    })
            );

        }
    }

}
