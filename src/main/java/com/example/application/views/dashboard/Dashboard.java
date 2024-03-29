package com.example.application.views.dashboard;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

public class Dashboard extends AppLayout {
    private final AuthService security;
    public Dashboard(AuthService security) {
        this.security = security;
        createHeader();
        createDrawer();
    }
    private void createHeader() {
        H1 logo = new H1("trainyWay");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", e -> {UI.getCurrent().getPage().setLocation(" ");
            VaadinSession.getCurrent().getSession().invalidate();
            VaadinSession.getCurrent().close();});

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo,
                logout
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void createDrawer() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        List<AuthService.AuthorizedRoute> routes = security.getAuthorizedRoutes(user.getRole());
        RouterLink[] list = new RouterLink[routes.size()];
        int index = 0;
        for(AuthService.AuthorizedRoute r : routes){
            RouterLink route = new RouterLink(r.route(), r.view());
            list[index++] = route;
        }
        addToDrawer(new VerticalLayout(list));
    }
}

