package com.example.application.views.ticket;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.Ticket;
import com.example.application.data.entity.Train;
import com.example.application.data.entity.User;
import com.example.application.data.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexWrap;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Position;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@PageTitle("trainyWay | buy tickets")
public class TicketView extends Div {

    private final TrainService trainService;

    private final TicketService ticketService;
    private final EmailService emailService;
    private final AuthService authService;

    private final StationService stationService;

    private String[] ticketDetails = new String[6];

    private static final List<String> trains = new LinkedList<>();
    private List<String> fromStations = new LinkedList<>();
    private List<String> toStations = new LinkedList<>();

    private static final List<String> seatNumbers = new LinkedList<>();


    private ComboBox<String> trainSelect;

    private ComboBox<String> routeDep;

    private ComboBox<String> routeArr;

    private ComboBox<String> wagonNumber;

    private ComboBox<String> seatNumber;

    private Paragraph ticketPrice;

    private DatePicker datePicker;

    static {

        for(int i = 1; i <= 60; ++i ){
            if(i % 2 == 0)
                seatNumbers.add(String.valueOf(i) + "\t[\uD83D\uDDD4]");
            else
                seatNumbers.add(String.valueOf(i));
        }

    }

    public TicketView(TrainService trainService, EmailService emailService, AuthService authService, TicketService ticketService, StationService stationService) {
        this.trainService = trainService;
        this.emailService = emailService;
        this.authService = authService;
        this.ticketService = ticketService;
        this.stationService = stationService;
        trains.clear();
        for(Train t : trainService.getAll()){
            trains.add(t.getTrainName() + " [" + t.getDepStation() + " -> " + t.getArrStation() + "]");
        }
        addClassNames("checkout-form-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        Main content = new Main();
        content.addClassNames(Display.GRID, Gap.XLARGE, AlignItems.START, JustifyContent.CENTER, MaxWidth.SCREEN_MEDIUM,
                Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        content.add(createCheckoutForm());
        add(content);
    }

    private Component createCheckoutForm() {
        Section checkoutForm = new Section();
        checkoutForm.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW);

        checkoutForm.add(createPersonalDetailsSection());
        checkoutForm.add(createShippingAddressSection());
        checkoutForm.add(new Hr());
        checkoutForm.add(createFooter());

        return checkoutForm;
    }

    private Section createPersonalDetailsSection() {
        User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
        Section personalDetails = new Section();
        personalDetails.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);

        H3 header = new H3("Personal details");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");
        name.addClassNames(Margin.Bottom.SMALL);
        name.setValue(currentUser.getUsername());

        EmailField email = new EmailField("Email address");
        email.setRequiredIndicatorVisible(true);
        email.addClassNames(Margin.Bottom.SMALL);
        email.setValue(currentUser.getEmail());

        TextField phone = new TextField("Phone number");
        phone.setRequiredIndicatorVisible(true);
        phone.setPattern("[\\d \\-\\+]+");
        phone.addClassNames(Margin.Bottom.SMALL);

        personalDetails.add(header, name, email, phone);
        return personalDetails;
    }

    private Section createShippingAddressSection() {
        Section shippingDetails = new Section();
        shippingDetails.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);


        H3 header = new H3("Train ticket details");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        trainSelect = new ComboBox<>("Train");
        trainSelect.setRequiredIndicatorVisible(true);
        trainSelect.addClassNames(Margin.Bottom.SMALL);

        datePicker = new DatePicker("Departure date");
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        datePicker.setMin(now);
        datePicker.setMax(now.plusDays(30));
        datePicker.setHelperText("Must be within 30 days from today");
        datePicker.setRequiredIndicatorVisible(true);
        datePicker.setVisible(false);
        datePicker.addClassNames(Margin.Bottom.SMALL);


        Div routeSelectionSection = new Div();
        routeSelectionSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        routeDep = new ComboBox<>("From");
        routeDep.setRequiredIndicatorVisible(true);
        routeDep.setItems(fromStations);
        routeDep.setVisible(false);
        routeDep.addClassNames(Margin.Bottom.SMALL);

        routeArr = new ComboBox<>("To");
        routeArr.setRequiredIndicatorVisible(true);
        routeArr.setItems("1", "2", "3", "4", "5", "6");
        routeArr.setVisible(false);
        routeArr.setEnabled(false);
        routeArr.addClassNames(Margin.Bottom.SMALL);

        routeSelectionSection.add(routeDep, routeArr);

        Div subSection = new Div();
        subSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        wagonNumber = new ComboBox<>("Wagon");
        wagonNumber.setRequiredIndicatorVisible(true);
        wagonNumber.setItems("1", "2", "3", "4", "5", "6");
        wagonNumber.setVisible(false);
        wagonNumber.addClassNames(Margin.Bottom.SMALL);

        seatNumber = new ComboBox<>("Seat");
        seatNumber.setRequiredIndicatorVisible(true);
        seatNumber.setItems(seatNumbers);
        seatNumber.setVisible(false);
        seatNumber.setEnabled(false);
        seatNumber.addClassNames(Margin.Bottom.SMALL);

        subSection.add(wagonNumber, seatNumber);

        trainSelect.setItems(trains);

        float price = 9.5f * 4;
        ticketPrice = new Paragraph("Total: " + price + " lei.");
        ticketPrice.setVisible(false);
        ticketPrice.addClassNames(Margin.Top.MEDIUM, FontSize.MEDIUM, TextColor.SECONDARY);

        wagonNumber.addValueChangeListener(e -> seatNumber.setEnabled(true));
        //trainSelect.addValueChangeListener(e -> {wagonNumber.setVisible(true); seatNumber.setVisible(true);});
        trainSelect.addValueChangeListener(e -> {datePicker.setVisible(true); updateStations(trainSelect.getValue());});
        datePicker.addValueChangeListener(e -> {routeDep.setVisible(true); routeArr.setVisible(true);});
        routeDep.addValueChangeListener(e -> {
            routeArr.setEnabled(true);
            updateArrStations(trainSelect.getValue(), routeDep.getValue());
        });
        routeArr.addValueChangeListener(e -> {
            updateTicketDetails(trainSelect.getValue(), routeDep.getValue(), routeArr.getValue(), wagonNumber.getValue(), seatNumber.getValue(), ticketPrice.getText());
            wagonNumber.setVisible(true); seatNumber.setVisible(true); updatePrice(ticketPrice, 4);});
        seatNumber.addValueChangeListener(e -> {

            ticketPrice.setVisible(true); updatePrice(ticketPrice, 4);
            updateTicketDetails(trainSelect.getValue(), routeDep.getValue(), routeArr.getValue(), wagonNumber.getValue(), seatNumber.getValue(), ticketPrice.getText());
        });

        shippingDetails.add(header, trainSelect, datePicker, routeSelectionSection, subSection, ticketPrice);
        return shippingDetails;
    }

    private void updateArrStations(String trainID, String depID){
        Optional<Train> t = trainService.get(trainID.split(" ")[0]);
        Optional<Station> st = stationService.get(depID.split(" ")[0]);
        toStations.clear();
        for(Station s : stationService.getAfterStations(t.get().getId(), st.get().getStationName())){
            toStations.add(s.getStationName() + " [" + s.getArrTime() + "]");
        }
        routeArr.setItems(toStations);
    }

    private void updateStations(String id){
        //System.out.println(id.split(" ")[0]);
        Optional<Train> t = trainService.get(id.split(" ")[0]);
        fromStations.clear();
        for(Station s : stationService.getStationsByTrainId(t.get().getId().toString())){
            fromStations.add(s.getStationName() + " [" + s.getDepTime() + "]");
        }
        routeDep.setItems(fromStations);
    }

    private Footer createFooter() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        Footer footer = new Footer();
        footer.addClassNames(Display.FLEX, AlignItems.CENTER, JustifyContent.BETWEEN, Margin.Vertical.MEDIUM);

        Button cancel = new Button("Cancel");
        cancel.addClickListener(event -> UI.getCurrent().navigate("home"));
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button pay = new Button("Buy ticket", new Icon(VaadinIcon.LOCK));
        pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        pay.addClickListener(event -> {
            if(trainSelect.isInvalid() || routeArr.isInvalid() || routeDep.isInvalid() || wagonNumber.isInvalid() || seatNumber.isInvalid() || datePicker.isInvalid() ||
                    trainSelect.isEmpty() || routeArr.isEmpty() || routeDep.isEmpty() || wagonNumber.isEmpty() || seatNumber.isEmpty() || datePicker.isEmpty()){
                Notification n = Notification.show("All fields must be filled!");
                n.setDuration(4000);
                n.setPosition(Notification.Position.BOTTOM_START);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else {
                UI.getCurrent().navigate("home");
                Notification n = Notification.show("Successfully bought the ticket, details will be sent to your email!");
                n.setDuration(4000);
                n.setPosition(Notification.Position.BOTTOM_START);
                n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                Thread inputThread = new Thread(() -> {
                    try {
                        emailService.sendTicketDetails("maroan0107@yahoo.com", ticketDetails);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
                inputThread.start();
            }
        });

        footer.add(cancel, pay);
        return footer;
    }

    private void updateTicketDetails(String... details){
        ticketDetails[0] = details[0];
        ticketDetails[1] = details[1];
        ticketDetails[2] = details[2];
        ticketDetails[3] = details[3];
        ticketDetails[4] = details[4];
        ticketDetails[5] = details[5].replace("Total: ", "");
    }


    public void updatePrice(Paragraph p, int value){
        p.setText("Total " + 9.5 * value + " lei.");
    }
}