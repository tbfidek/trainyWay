package com.example.application.views.dashboard;

import com.example.application.data.entity.Review;
import com.example.application.data.entity.User;
import com.example.application.data.service.AuthService;
import com.example.application.data.service.ReviewRepository;
import com.example.application.data.service.ReviewService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainDashboard extends AppLayout {
    private final AuthService security;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private TextField code;
    private Select<String> select, select1, select2;
    private String selectedTrainId;

    public TrainDashboard(AuthService security, ReviewService reviewService, ReviewRepository reviewRepository) {
        this.security = security;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("trainyWay");
        logo.addClassNames("text-l", "m-m");


        Dialog review = new Dialog();
        review.setHeaderTitle("Review your journey!");
        Button cancelButton = new Button("Cancel", e -> review.close());
        Button saveReviewButton = new Button("Review", e -> {
            if (code.isEmpty() || select.isEmpty() || select1.isEmpty() || select2.isEmpty()) {
                select2.setHelperText("Make sure all fields are completed");
            } else {
                UI.getCurrent().getPage().executeJs("return window.location.href").then(String.class, location -> {
                    selectedTrainId = location.split("/")[4];
                    User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
                    if (reviewService.verifyCode(code.getValue().trim(), Long.valueOf(selectedTrainId), currentUser.getId())) {
                        float reviewScore = (Float.valueOf(select.getValue()) + Float.valueOf(select1.getValue()) + Float.valueOf(select2.getValue())) / 3;
                        reviewRepository.save(new Review(currentUser.getId(), Long.valueOf(selectedTrainId), reviewScore));
                        review.close();
                        Notification n = Notification.show("Thanks for the review! If it's not positive fucking kill yourself!");
                        n.setDuration(4000);
                        n.setPosition(Notification.Position.MIDDLE);
                        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    } else {
                        select2.setHelperText("");
                        code.setHelperText("Make sure the code is valid and yours and that you didn't already review the train"); // or the code isn't yours

                    }
                });

            }
        });


        review.getFooter().add(cancelButton);
        review.getFooter().add(saveReviewButton);

        Button logout = new Button("Log out", e -> {
            UI.getCurrent().getPage().setLocation(" ");
            VaadinSession.getCurrent().getSession().invalidate();
            VaadinSession.getCurrent().close();
        });
        Button reviewButton = new Button("Review", e -> review.open());

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo,
                reviewButton,
                logout
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        VerticalLayout popup = createDialogLayout();
        review.add(popup);
        addToNavbar(header);

    }

    private VerticalLayout createDialogLayout() {

        code = new TextField("please enter the ticket code:");
        VerticalLayout dialogLayout = new VerticalLayout(code);

        List<String> rating = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        List<Select> list = new ArrayList<>();
        select = new Select<>();
        list.add(select);
        select.setLabel("rate cleanliness level");

        select1 = new Select<>();
        list.add(select1);
        select1.setLabel("rate timeliness level");

        select2 = new Select<>();
        list.add(select2);
        select2.setLabel("rate accessibility level");

        for (Select s : list) {
            s.setItems(rating);
            dialogLayout.add(s);
        }

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private void createDrawer() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        List<AuthService.AuthorizedRoute> routes = security.getAuthorizedRoutes(user.getRole());
        RouterLink[] list = new RouterLink[routes.size()];
        int index = 0;
        for (AuthService.AuthorizedRoute r : routes) {
            RouterLink route = new RouterLink(r.route(), r.view());
            list[index++] = route;
        }
        addToDrawer(new VerticalLayout(list));
    }
}


