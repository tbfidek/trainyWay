package com.example.application.views.main;

import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Main")
public class MainView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public MainView() {
        name = new TextField("Your name");
        User u = VaadinSession.getCurrent().getAttribute(User.class);
        sayHello = new Button("Say hello " + u.getUsername());
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }

}
