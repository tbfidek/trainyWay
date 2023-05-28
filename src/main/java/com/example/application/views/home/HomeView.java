package com.example.application.views.home;

import com.example.application.data.entity.Train;
import com.example.application.data.service.TrainRepository;
import com.example.application.data.service.TrainService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@PageTitle("Home")
@Uses(Icon.class)
public class HomeView extends VerticalLayout {

    private final String SAMPLEPERSON_ID = "trainID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "train/%s";

    private final Grid<Train> grid = new Grid<>(Train.class, false);
    private Train train;
    private final TrainService trainService;
    private final TrainRepository trainRepository;

    public HomeView(TrainService trainService, TrainRepository trainRepository) {

        this.trainService = trainService;
        this.trainRepository = trainRepository;
        addClassNames("home-view");
        add(grid);
        Dialog dialog = new Dialog();
        getElement().getStyle().set("height", "100%");
        grid.setHeight("100%");
        grid.addColumn("trainName").setAutoWidth(true).setHeader("train");
        grid.addColumn("depStation").setAutoWidth(true).setHeader("departure station");
        grid.addColumn(station -> formatTime(station.getDepTime())).setAutoWidth(true).setHeader("departure time");
        grid.addColumn("arrStation").setAutoWidth(true).setHeader("arrival station");
        grid.addColumn(station -> formatTime(station.getArrTime())).setAutoWidth(true).setHeader("arrival time");
        grid.addColumn("delay").setAutoWidth(true).setHeader("delay");
        grid.setItems(query -> trainService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        GridContextMenu<Train> menu = grid.addContextMenu();
        menu.addItem("Review", event -> {
            dialog.open();
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // train page redirect
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                UI.getCurrent().navigate(HomeView.class);
            }
        });

        dialog.setHeaderTitle("Review your journey!");

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button reviewButton = new Button("Review", e -> dialog.close());
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(reviewButton);

        add(dialog);

    }

    private String formatTime(Integer epochSeconds) {
        String formattedTime = "";
        if(epochSeconds != 0){
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
            formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm", new Locale("ro_RO")));

        }
        return formattedTime;
    }

    private static VerticalLayout createDialogLayout() {

        TextField firstNameField = new TextField("Stars");
        TextField lastNameField = new TextField("Message");

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField,
                lastNameField);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

}
