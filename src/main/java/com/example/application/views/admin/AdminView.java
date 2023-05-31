package com.example.application.views.admin;
import com.example.application.data.entity.Train;
import com.example.application.data.service.TrainService;
import com.example.application.utils.Broadcaster;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import static com.example.application.utils.Utils.formatTime;
import static com.example.application.utils.Utils.replaceSearch;

@PageTitle("Admin")
@Uses(Icon.class)
public class AdminView extends Div {
    private final Grid<Train> grid = new Grid<>(Train.class, false);
    private TextField trainName, depStation, depTime, arrStation, arrTime, delay;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final TextField searchField = new TextField();

    private final BeanValidationBinder<Train> binder;

    private Train train;

    private final TrainService trainService;


    public AdminView(TrainService trainService) throws IOException {

        this.trainService = trainService;
        addClassNames("admin-view");
        FileBuffer fileBuffer = new FileBuffer();

        Upload upload = new Upload(fileBuffer);
        upload.setAcceptedFileTypes(".csv");

        upload.addSucceededListener(event -> {
            FileData savedFileData = fileBuffer.getFileData();
            String originalFileName = savedFileData.getFileName();

            if (originalFileName.endsWith(".csv")) {
                File destinationDirectory = new File("D:/STUDENT");
                File destinationFile = new File(destinationDirectory, "input.csv");

                savedFileData.getFile().renameTo(destinationFile);
                InputStream fis = fileBuffer.getInputStream();
                byte[] cfff;
                try {
                    cfff = new byte[fis.available()];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    fis.read(cfff);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String fileContent = new String(cfff);
                System.out.println(fileContent);
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                trainService.uploadTrain(fileContent);

                System.out.printf("File saved to: %s%n", destinationFile.getAbsolutePath());
            } else {
                savedFileData.getFile().delete();
                Notification.show("Only .csv files are allowed");
            }
        });
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(searchField, upload, splitLayout);

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

        splitLayout.setHeightFull();
        splitLayout.getElement().getStyle().set("height", "100%");
        getElement().getStyle().set("height", "100%");

        grid.setSizeFull();
        grid.addColumn("trainName").setAutoWidth(true).setHeader("train");
        grid.addColumn("depStation").setAutoWidth(true).setHeader("departure station");
        grid.addColumn(station -> formatTime(station.getDepTime())).setAutoWidth(true).setHeader("departure time");
        grid.addColumn("arrStation").setAutoWidth(true).setHeader("arrival station");
        grid.addColumn(station -> formatTime(station.getArrTime())).setAutoWidth(true).setHeader("arrival time");
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
                    //this.train.setId(trainService.getMaxTrainId() + 1);
                }
                binder.writeBean(this.train);
                Broadcaster.broadcast(this.train.getTrainName() + " was delayed by " + this.train.getDelay() + " minutes" );
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
