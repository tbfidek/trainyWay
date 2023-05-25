package com.example.application.views.home;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.User;
import com.example.application.data.service.StationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("train/:trainID")
public class TrainView extends Div implements BeforeEnterObserver {

    private String trainID;

    public TrainView(StationService stationService) {

        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            addClassNames("train-view");
            addClassNames("admin-view");
            // Create UI
            getElement().getStyle().set("height", "100%");
            Grid<Station> grid = new Grid<>(Station.class, false);
            grid.setSizeFull();
            grid.addColumn("stationName").setAutoWidth(true).setHeader("station");
            grid.addColumn("depTime").setAutoWidth(true).setHeader("departure time");
            grid.addColumn("arrTime").setAutoWidth(true).setHeader("arrival time");
            grid.setHeightFull();
            addAttachListener(attachEvent -> {
                grid.setItems(stationService.getStationsByTrainId(trainID));
            });
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            add(grid);

        } else {
            UI.getCurrent().getPage().executeJs("document.location = '/';");

        }
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
            trainID = beforeEnterEvent.getRouteParameters().get("trainID").get();
    }
}
