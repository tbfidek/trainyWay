package com.example.application.views.login;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

//@CssImport("../frontend/themes/mersultrenurilor/views/login-view.css")
@PageTitle("Login")
@Route("")
public class LoginView extends Div {

    public LoginView(AuthService authService) {
        setId("login-view");
        var username = new TextField("username");
        var password = new PasswordField("password");
        add(
                new H1("Welcomee"),
                username,
                password,
                new Button("Login", buttonClickEvent ->{
                    try {
                        authService.authenticate(username.getValue(), password.getValue());
                        UI.getCurrent().navigate("home");
                    } catch (AuthService.AuthException e) {
                        Notification.show("Wrong credentials");
                    }
                })
        );

    }

}
