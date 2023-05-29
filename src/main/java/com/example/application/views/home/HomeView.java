package com.example.application.views.home;

import com.example.application.data.entity.Train;
import com.example.application.data.service.ReviewService;
import com.example.application.data.service.TrainRepository;
import com.example.application.data.service.TrainService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.List;
import static com.example.application.utils.Utils.formatTime;

@PageTitle("Home")
@Uses(Icon.class)
public class HomeView extends VerticalLayout {
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "train/%s";
    private final Grid<Train> grid = new Grid<>(Train.class, false);
    private final TextField searchField = new TextField();
    private Train train;
    private final TrainService trainService;
    private final TrainRepository trainRepository;
    private final ReviewService reviewService;

    public HomeView(TrainService trainService, TrainRepository trainRepository, ReviewService reviewService) {

        this.trainService = trainService;
        this.trainRepository = trainRepository;
        this.reviewService = reviewService;
        addClassNames("home-view");
        add(searchField, grid);
        getElement().getStyle().set("height", "100%");
        grid.setHeight("100%");
        grid.addColumn("trainName").setAutoWidth(true).setHeader("train");
        grid.addColumn("depStation").setAutoWidth(true).setHeader("departure station");
        grid.addColumn(station -> formatTime(station.getDepTime())).setAutoWidth(true).setHeader("departure time");
        grid.addColumn("arrStation").setAutoWidth(true).setHeader("arrival station");
        grid.addColumn(station -> formatTime(station.getArrTime())).setAutoWidth(true).setHeader("arrival time");
        grid.addColumn("delay").setAutoWidth(true).setHeader("delay");
        grid.addColumn(p -> reviewService.ratingScore(p.getId())).setAutoWidth(true).setHeader("rating");


        List<Train> trainList = trainService.getAll();
        GridListDataView<Train> gridView = grid.setItems(trainList);

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> gridView.refreshAll());

        gridView.addFilter(train -> {
            String searchTerm = searchField.getValue().trim();
            if (searchTerm.isEmpty())
                return true;
            boolean matchesName = replaceSearch(train.getTrainName()).contains(searchTerm.toLowerCase());
            boolean matchesArr = replaceSearch(train.getArrStation()).contains(searchTerm.toLowerCase());
            boolean matchesDep = replaceSearch(train.getDepStation()).contains(searchTerm.toLowerCase());
            return matchesName || matchesArr || matchesDep;
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

    }
    private String replaceSearch(String search){

        String convertedString =
                Normalizer
                        .normalize(search, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");
        return convertedString.toLowerCase();
    }
}
