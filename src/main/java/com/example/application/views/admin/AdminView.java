package com.example.application.views.admin;
import com.example.application.data.entity.Train;
import com.example.application.data.service.TrainService;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Admin")
@Uses(Icon.class)
public class AdminView extends Div {
    private final Grid<Train> grid = new Grid<>(Train.class, false);
    private TextField trainName;
    private TextField depStation;
    private TextField depTime;
    private TextField arrStation;
    private TextField arrTime;
    private TextField delay;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Train> binder;

    private Train train;

    private final TrainService trainService;
    private final UserRepository userRepository;

    public AdminView(TrainService trainService, UserRepository userRepository) {

        this.trainService = trainService;
        this.userRepository = userRepository;
        addClassNames("admin-view");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
        splitLayout.setHeightFull();
        splitLayout.getElement().getStyle().set("height", "100%");
        getElement().getStyle().set("height", "100%");
        grid.setSizeFull();
        // Configure Grid
        grid.addColumn("trainName").setAutoWidth(true).setHeader("train");
        grid.addColumn("depStation").setAutoWidth(true).setHeader("departure station");
        grid.addColumn("depTime").setAutoWidth(true).setHeader("departure time");
        grid.addColumn("arrStation").setAutoWidth(true).setHeader("arrival station");
        grid.addColumn("arrTime").setAutoWidth(true).setHeader("arrival time");
        grid.addColumn("delay").setAutoWidth(true).setHeader("delay");
        grid.setHeightFull();
        grid.setItems(query -> trainService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Train> tren = trainService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (tren.isPresent()) {
                    populateForm(tren.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Train.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.train == null) {
                    this.train = new Train();
                    this.train.setId(trainService.getMaxTrainId() + 1);
                }
                binder.writeBean(this.train);
                trainService.update(this.train);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AdminView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        trainName = new TextField("train id");
        depStation = new TextField("departure station");
        depTime = new TextField("departure time");
        arrStation = new TextField("arrival station");
        arrTime = new TextField("arrival time");
        delay = new TextField("delay in minutes");
        formLayout.setSizeFull();
        formLayout.add(trainName, delay, depStation, depTime, arrStation, arrTime);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        splitLayout.addToPrimary(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Train train) {
        this.train = train;
        binder.readBean(this.train);

    }
}
