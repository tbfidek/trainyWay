package com.example.application.views.authentication;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
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

@CssImport("../frontend/styles/views/auth/signup-view.css")
@PageTitle("trainyWay | signup")
@Route("signup")
public class SignupView extends Div {

    public SignupView(AuthService authService) {
            setId("signup-view");
            var username = new TextField("username");
            var email = new TextField("email");
            var password = new PasswordField("password");
            var confirmPassword = new PasswordField("confirm password");
            if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
                UI.getCurrent().getPage().executeJs("document.location = '/home';");
            } else {
            add(
                    new H1("Welcome"),
                    username,
                    email,
                    password,
                    confirmPassword,
                    new Button("Sign up", buttonClickEvent -> {
                        try {
                            authService.register(username.getValue(), email.getValue(), password.getValue(), confirmPassword.getValue());
                            UI.getCurrent().navigate(" ");
                        } catch (AuthService.PasswordNotEqualException e) {
                            Notification.show("Your passwords are not equal!");
                        } catch (AuthService.UserAlreadyExistsException e) {
                            Notification.show("Entered email is already in use!");
                        } catch (AuthService.BlankFieldsException e) {
                            Notification.show("All fields must be completed!");

                        }
                    })
            );

        }
    }

}