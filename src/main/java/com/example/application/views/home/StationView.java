package com.example.application.views.home;

import com.example.application.data.entity.Station;
import com.example.application.data.service.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexWrap;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.util.*;

@PageTitle("trainyWay | station schedule")
public class StationView extends Div {
    private final TrainService trainService;
    private final StationService stationService;
    private List<String> fromStations = new LinkedList<>();
    private ComboBox<String> routeDep;
    private Grid<Station> arrivals;
    private Grid<Station> departures;

    public StationView(TrainService trainService, StationService stationService) {
        this.trainService = trainService;
        this.stationService = stationService;
        addClassNames("station-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);
        System.out.println(stationService.arrivalsList("Tecuci"));
        H2 arrLabel = new H2("Arrivals");
        H2 depLabel = new H2("Departures");
        Section test = new Section(arrLabel, depLabel);
        test.setWidth("100%");
        test.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.JustifyContent.AROUND);
        VerticalLayout header = new VerticalLayout(
                createDialogLayout(),
                test,
                stationGrids()
        );
        header.setHeight("100%");
        header.setWidth("100%");
        add(header);

        routeDep.addValueChangeListener(e -> {


            routeDep.clear();
        });
    }

    private VerticalLayout createDialogLayout() {
        H3 header = new H3("Enter station");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        Div routeSelectionSection = new Div();
        routeSelectionSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        routeDep = new ComboBox<>("Station");
        routeDep.setRequiredIndicatorVisible(true);
        routeDep.setItems(fromStations);
        routeDep.setValue("Tecuci");

        routeDep.setVisible(true);
        routeDep.addClassNames(Margin.Bottom.SMALL);
        routeSelectionSection.add(routeDep);
        //EVENTS
        updateStations();
//        routeDep.addValueChangeListener(e -> {
//
//            if (routeDep.getValue().equals("")) {
//                routeDep.setHelperText("Please select a station");
//            }
//        });
        VerticalLayout dialogLayout = new VerticalLayout(routeSelectionSection);

        return dialogLayout;
    }

    private HorizontalLayout stationGrids() {
        HorizontalLayout grids = new HorizontalLayout();
        List<String> data = stationService.arrivalsList("Tecuci");

        H3 trainName = new H3("train");
        H3 arrivesAt = new H3("arrives at");
        H3 from = new H3("from");
        Section test = new Section(trainName, arrivesAt, from);
        test.setWidth("100%");
        test.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.JustifyContent.AROUND);

        for (String s : data) {
            System.out.println(s);

            String[] row = s.split("\\),");

            for (int i = 0; i < row.length; i++) {
                Section cf = new Section();
                String[] idk = row[i].split(",");
                System.out.println(row[i]);
                for (int j = 0; j < 3; j++) {
                    Label c = new Label(idk[j]);
                    cf.add(c);
                }
                cf.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.JustifyContent.AROUND);
                grids.add(cf);
            }

        }

        arrivals = new Grid(Station.class, false);
        departures = new Grid(Station.class, false);
        arrivals.setSizeFull();
        departures.setSizeFull();
        arrivals.addColumn(p -> p.getArrTime()).setAutoWidth(true).setHeader("Train");
        arrivals.addColumn(p -> p.getArrTime()).setAutoWidth(true).setHeader("From");
        arrivals.addColumn(p -> p.getArrTime()).setAutoWidth(true).setHeader("Arrival");
        departures.addColumn(p -> p.getStationaryTime()).setAutoWidth(true).setHeader("Train");
        departures.addColumn(p -> p.getStationaryTime()).setAutoWidth(true).setHeader("To");
        departures.addColumn(p -> p.getStationaryTime()).setAutoWidth(true).setHeader("Departure");
        addAttachListener(attachEvent -> {
            List<Station> stations = stationService.getStationsByTrainId("22");
            arrivals.setItems(stations);
            departures.setItems(stations);
        });

        grids.setHeight("100%");
        grids.setWidth("100%");
        grids.addClassNames(Display.FLEX, FlexDirection.COLUMN, LumoUtility.JustifyContent.AROUND);
        return grids;
    }

    private void updateStations() {
        fromStations.clear();
        for (String st : stationService.stationList()) {
            fromStations.add(st);
        }
        routeDep.setItems(fromStations);
    }
}