package com.example.application.views.home;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.User;
import com.example.application.data.service.StationService;
import com.example.application.views.dashboard.TrainDashboard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.List;
import static com.example.application.utils.Utils.*;

@Route(value="train/:trainID", layout= TrainDashboard.class)
public class TrainView extends Div implements BeforeEnterObserver {

    private String trainID;
    private final StationService stationService;

    public TrainView(StationService stationService) {
        this.stationService = stationService;

        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            addClassNames("train-view", "admin-view");
            getElement().getStyle().set("height", "100%");

            Grid<Station> grid = new Grid<>(Station.class, false);
            grid.setSizeFull();
            grid.addColumn(station -> formatTime(station.getArrTime())).setAutoWidth(true).setHeader("arrival time");
            grid.addColumn("stationName").setAutoWidth(true).setHeader("station");
            grid.addColumn(station -> setBreak(station.getStationaryTime())).setAutoWidth(true).setHeader("break");
            grid.addColumn(station -> formatTime(station.getDepTime())).setAutoWidth(true).setHeader("departure time");
            grid.addColumn(p -> updateDelay()).setAutoWidth(true).setHeader("delay");
            grid.setHeightFull();

            addAttachListener(attachEvent -> {
                List<Station> stations = stationService.getStationsByTrainId(trainID);
                grid.setItems(stations);
            });

            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            add(grid);
        } else {
            UI.getCurrent().getPage().executeJs("document.location = '/';");
        }

    }

    public String updateDelay(){
    return null;
    }
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        trainID = beforeEnterEvent.getRouteParameters().get("trainID").get();
    }
}