package com.example.application.data.service;

import com.example.application.Exceptions.AuthException;
import com.example.application.Exceptions.BlankFieldsException;
import com.example.application.Exceptions.PasswordNotEqualException;
import com.example.application.Exceptions.UserAlreadyExistsException;
import com.example.application.data.Role;
import com.example.application.data.entity.User;
import com.example.application.views.admin.AdminView;
import com.example.application.views.home.HomeView;
import com.example.application.views.dashboard.Dashboard;
import com.example.application.views.home.StationView;
import com.example.application.views.ticket.TicketView;
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
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    private void createRoutes(Role role) {
        getAuthorizedRoutes(role).forEach(route -> RouteConfiguration.forSessionScope().setRoute(
                route.route, route.view, Dashboard.class));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(Role role) {
        var routes = new ArrayList<AuthorizedRoute>();

        if (role.equals(Role.USER)) {
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("ticket", "Ticket", TicketView.class));
            routes.add(new AuthorizedRoute("station", "Station", StationView.class));
        } else if (role.equals(Role.DISPATCHER)) {
            routes.add(new AuthorizedRoute("admin", "Admin", AdminView.class));
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
        }
        return routes;
    }
}