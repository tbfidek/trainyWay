package com.example.application.views.home;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.Train;
import com.example.application.data.entity.User;
import com.example.application.data.service.StationService;
import com.example.application.data.service.TrainService;
import com.example.application.views.dashboard.TrainDashboard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static com.example.application.utils.Utils.*;

@Route(value = "train/:trainID", layout = TrainDashboard.class)
@CssImport(value = "../frontend/styles/views/train/train-view.css")
@PageTitle("trainyWay | train view")
public class TrainView extends Div implements BeforeEnterObserver {

    private String trainID;
    private final StationService stationService;
    private static TrainService trainService = null;

    public TrainView(StationService stationService, TrainService trainService) {
        this.stationService = stationService;
        this.trainService = trainService;

        if (VaadinSession.getCurrent().getAttribute(User.class) != null) {
            addClassNames("train-view", "admin-view");
            getElement().getStyle().set("height", "100%");

            H1 train = new H1("");

            Grid<Station> grid = new Grid<>(Station.class, false);
            grid.setSizeFull();
            grid.addColumn(station -> formatTime(station.getArrTime(), station.getDepTime())).setAutoWidth(true).setHeader("arrival time");
            grid.addColumn("stationName").setAutoWidth(true).setHeader("station");
            grid.addColumn(station -> setBreak(station.getStationaryTime())).setAutoWidth(true).setHeader("break");
            grid.addColumn(station -> formatTime(station.getDepTime(), station.getDepTime(), station.getArrTime())).setAutoWidth(true).setHeader("departure time");
            grid.addColumn(createToggleDetailsRenderer(grid));

            grid.setDetailsVisibleOnClick(false);
            grid.setItemDetailsRenderer(createTrainDetailsRenderer());
            grid.setHeightFull();

            addAttachListener(attachEvent -> {
                List<Station> stations = stationService.getStationsByTrainId(trainID);
                grid.setItems(stations);
                train.setText(trainService.get(Long.valueOf(trainID)).get().getTrainName());
                train.addClassNames("text-l", "m-m");
            });

            grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
            add(train, grid);
        } else {
            UI.getCurrent().getPage().executeJs("document.location = '/';");
        }

    }

    private static Renderer<Station> createToggleDetailsRenderer(
            Grid<Station> grid) {
        return LitRenderer.<Station> of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">details</vaadin-button>")
                .withFunction("handleClick",
                        person -> grid.setDetailsVisible(person,
                                !grid.isDetailsVisible(person)));
    }

    private static ComponentRenderer<TrainDetailsFormLayout, Station> createTrainDetailsRenderer() {
        return new ComponentRenderer<>(TrainDetailsFormLayout::new,
                TrainDetailsFormLayout::setPerson);
    }
    private static class TrainDetailsFormLayout extends FormLayout {
        private final TextField delayField = new TextField("delay");


        public TrainDetailsFormLayout() {
            delayField.setReadOnly(true);
                add(delayField);

            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(delayField, 3);

        }

        public void setPerson(Station person) {
            delayField.setValue(updateTrains(person));

        }
    }

    private boolean isStationDelayed(Station station) {
        Optional<Train> tren = trainService.get(Long.valueOf(station.getTrainID()));
        return tren.get().getDelay() > 0;
    }

    public static String updateTrains(Station st) {
            Optional<Train> tren = trainService.get(Long.valueOf(st.getTrainID()));
            if (tren.get().getDelay() > 0) {
                long date = st.getDepTime() - st.getStationaryTime();
                if (date != 0) {
                    Date nowDate = new Date(System.currentTimeMillis());

                    Calendar nowCalendar = Calendar.getInstance();

                    nowCalendar.setTime(nowDate);

                    Calendar trainArrivalCalendar = Calendar.getInstance();
                    trainArrivalCalendar.setTimeInMillis(date * 1000);

                    trainArrivalCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
                    int trainH = trainArrivalCalendar.get(Calendar.HOUR_OF_DAY);
                    int nowH = nowCalendar.get(Calendar.HOUR_OF_DAY);
                    int trainM = trainArrivalCalendar.get(Calendar.MINUTE);
                    int nowM = nowCalendar.get(Calendar.MINUTE);

                    LocalTime trainArrivalTime = LocalTime.of(trainH, trainM);
                    LocalTime currentTime = LocalTime.of(nowH, nowM);

                    Duration timeDifference = Duration.between(currentTime, trainArrivalTime);
                    if (timeDifference.getSeconds() / 60 > 0) {
                        if (Math.abs(timeDifference.getSeconds() / 60) > 1000) {
                            //System.out.println("A TRECUT!");
                            return "not delayed";
                        } else {
                            //System.out.println("NU A TRECUT!");
                            return tren.get().getDelay().toString() + " minutes";
                        }
                    } else {
                        if (Math.abs(timeDifference.getSeconds() / 60) > 1000) {
                            return tren.get().getDelay().toString() + " minutes";

                        } else {
                            return "not delayed";
                        }
                    }
                }
            }
            return "not delayed";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        trainID = beforeEnterEvent.getRouteParameters().get("trainID").get();
    }
}