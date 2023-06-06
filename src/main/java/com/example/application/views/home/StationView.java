package com.example.application.views.home;

import com.example.application.data.service.*;
import com.example.application.utils.Utils;
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
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.util.*;

import static com.example.application.utils.Utils.replaceSearch;

@PageTitle("trainyWay | station schedule")
public class StationView extends Div {
    private final StationService stationService;
    private final List<String> stations = new LinkedList<>();
    private ComboBox<String> stationPicker;
    private Grid<List<String>> arrivals;
    private Grid<List<String>> departures;

    public StationView(StationService stationService) {
        this.stationService = stationService;
        addClassNames("station-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);
        H2 arrLabel = new H2("Arrivals");
        H2 depLabel = new H2("Departures");
        Section test = new Section(arrLabel, depLabel);
        test.setWidth("100%");
        test.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.JustifyContent.AROUND);
        VerticalLayout header = new VerticalLayout(
                createStationLayout(),
                test,
                stationGrids()
        );
        header.setHeight("100%");
        header.setWidth("100%");
        add(header);


        stationPicker.addValueChangeListener(e -> {
            arrivals.setItems(stationService.arrivalsList(stationPicker.getValue()));
            departures.setItems(stationService.departuresList(stationPicker.getValue()));
//            String sanitizedStation = replaceSearch(e.getValue());
//
//            sanitizedStation = sanitizedStation.toLowerCase();
//            arrivals.setItems(stationService.arrivalsList(sanitizedStation));
//            departures.setItems(stationService.departuresList(sanitizedStation));
        });
    }

    private VerticalLayout createStationLayout() {

        Div routeSelectionSection = new Div();
        routeSelectionSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        stationPicker = new ComboBox<>("Station");
        stationPicker.setRequiredIndicatorVisible(true);
        stationPicker.setItems(stations);
        stationPicker.setValue("Bentu h.");

        stationPicker.setVisible(true);
        stationPicker.addClassNames(Margin.Bottom.SMALL);
        routeSelectionSection.add(stationPicker);
        updateStations();

        return new VerticalLayout(routeSelectionSection);
    }

    private HorizontalLayout stationGrids() {
        HorizontalLayout grids = new HorizontalLayout();
        arrivals = new Grid<>();
        departures = new Grid<>();
        arrivals.setSizeFull();
        departures.setSizeFull();
        arrivals.addColumn(list -> list.get(0)).setAutoWidth(true).setHeader("Train");
        arrivals.addColumn(list -> list.get(2)).setAutoWidth(true).setHeader("From");
        arrivals.addColumn(list -> Utils.formatTime(Integer.valueOf(list.get(1).trim()))).setAutoWidth(true).setHeader("Arrival");
        departures.addColumn(list -> list.get(0)).setAutoWidth(true).setHeader("Train");
        departures.addColumn(list -> list.get(2)).setAutoWidth(true).setHeader("To");
        departures.addColumn(list -> Utils.formatTime(Integer.valueOf(list.get(1).trim()))).setAutoWidth(true).setHeader("Departure");
        addAttachListener(attachEvent -> stationPicker.setValue("Bentu h."));

        grids.setHeight("100%");
        grids.setWidth("100%");
        grids.add(arrivals);
        grids.add(departures);
        grids.addClassNames(Display.FLEX, FlexDirection.ROW, LumoUtility.JustifyContent.CENTER);
        return grids;
    }

    private void updateStations() {
        stations.clear();
        stations.addAll(stationService.stationList());
        stationPicker.setItems(stations);
    }
}