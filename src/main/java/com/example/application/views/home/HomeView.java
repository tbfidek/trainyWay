package com.example.application.views.home;

import com.example.application.data.entity.Train;
import com.example.application.data.service.TrainRepository;
import com.example.application.data.service.TrainService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;

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
        getElement().getStyle().set("height", "100%");
        grid.setHeight("100%");
        grid.addColumn("trainName").setAutoWidth(true).setHeader("train");
        grid.addColumn("depStation").setAutoWidth(true).setHeader("departure station");
        grid.addColumn("depTime").setAutoWidth(true).setHeader("departure time");;
        grid.addColumn("arrStation").setAutoWidth(true).setHeader("arrival station");;
        grid.addColumn("arrTime").setAutoWidth(true).setHeader("arrival time");;
        grid.addColumn("delay").setAutoWidth(true).setHeader("delay");;
        grid.setItems(query -> trainService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        // train page redirect
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                UI.getCurrent().navigate(HomeView.class);
            }
        });

    }
}
