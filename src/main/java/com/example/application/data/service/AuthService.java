package com.example.application.data.service;

import com.example.application.data.Role;
import com.example.application.data.entity.User;
import com.example.application.views.admin.AdminView;
import com.example.application.views.home.HomeView;
import com.example.application.views.logout.LogoutView;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {

    }

    //SEPARA EXCEPTII IN PACKAGE SEPARAT;
    public class AuthException extends Exception {
    }

    public class UserAlreadyExistsException extends Exception {
    }

    public class PasswordNotEqualException extends Exception {
    }

    public class BlankFieldsException extends Exception {

    }

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        //userRepository.save(new User("t2", "p2", Role.USER));
        //userRepository.save(new User("freaks","test@yahoo.com", "freaks", Role.DISPATCHER));

    }

    public void register(String username, String email, String password, String confirmPassword) throws UserAlreadyExistsException, PasswordNotEqualException, BlankFieldsException {

        if (username.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
            throw new BlankFieldsException();
        } else {
            User user = userRepository.getByEmail(email);

            if (user != null) {
                throw new UserAlreadyExistsException();
            } else {
                if (!password.equals(confirmPassword)) {
                    throw new PasswordNotEqualException();
                } else {
                    userRepository.save(new User(username, email, password, Role.USER));
                }
            }
        }
    }

    public void authenticate(String username, String password) throws AuthException {
        User user = userRepository.getByUsername(username);
        if (user != null && user.checkPassword(password)) {
            createRoutes(user.getRole());
            VaadinSession.getCurrent().setAttribute(User.class, user);
        } else {
            throw new AuthException();
        }
    }

    //DE ADAUGAT AL 3 LEA PARAMETRU
    private void createRoutes(Role role) {
        getAuthorizedRoutes(role).forEach(route ->
                RouteConfiguration.forSessionScope().setRoute(
                        route.route, route.view));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(Role role) {
        var routes = new ArrayList<AuthorizedRoute>();

        if (role.equals(Role.USER)) {
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("main", "Main", MainView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
        } else if (role.equals(Role.DISPATCHER)) {
            routes.add(new AuthorizedRoute("admin", "Admin", AdminView.class));
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
            routes.add(new AuthorizedRoute("main", "Main", MainView.class));

        }
        return routes;
    }
}
