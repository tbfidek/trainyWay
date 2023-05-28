package com.example.application.views.ticket;

import com.example.application.data.entity.Station;
import com.example.application.data.entity.Ticket;
import com.example.application.data.entity.Train;
import com.example.application.data.entity.User;
import com.example.application.data.service.*;
import com.example.application.utils.Utils;
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
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexWrap;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.Date;

@PageTitle("trainyWay | buy tickets")
public class TicketView extends Div {

    private final TrainService trainService;

    private TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final EmailService emailService;
    private final AuthService authService;

    private final StationService stationService;

    private String[] ticketDetails = new String[10];

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

    TextField name;
    EmailField email;

    String review_code;

    static {

        for(int i = 1; i <= 60; ++i ){
            if(i % 2 == 0)
                seatNumbers.add(String.valueOf(i) + "\t[\uD83D\uDDD4]");
            else
                seatNumbers.add(String.valueOf(i));
        }

    }

    public TicketView(TrainService trainService, EmailService emailService, AuthService authService, TicketService ticketService, StationService stationService, TicketRepository ticketRepository) {
        this.trainService = trainService;
        this.emailService = emailService;
        this.authService = authService;
        this.ticketService = ticketService;
        this.stationService = stationService;
        this.ticketRepository = ticketRepository;
        trains.clear();
        addClassNames("checkout-form-view");
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        Main content = new Main();
        content.addClassNames(Display.GRID, Gap.XLARGE, AlignItems.START, JustifyContent.CENTER, MaxWidth.SCREEN_MEDIUM,
                Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        content.add(createCheckoutForm());
        add(content);
        review_code = RandomStringUtils.randomAlphanumeric(8);
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

        name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");
        name.addClassNames(Margin.Bottom.SMALL);
        name.setValue(currentUser.getUsername());

        email = new EmailField("Email address");
        email.setRequiredIndicatorVisible(true);
        email.addClassNames(Margin.Bottom.SMALL);
        email.setValue(currentUser.getEmail());

        personalDetails.add(header, name, email);
        return personalDetails;
    }

    private Section createShippingAddressSection() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        Section shippingDetails = new Section();
        shippingDetails.addClassNames(Display.FLEX, FlexDirection.COLUMN, Margin.Bottom.XLARGE, Margin.Top.MEDIUM);

        H3 header = new H3("Train ticket details");
        header.addClassNames(Margin.Bottom.MEDIUM, Margin.Top.SMALL, FontSize.XXLARGE);

        datePicker = new DatePicker("Departure date");
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        datePicker.setMin(now);
        datePicker.setMax(now.plusDays(7));
        datePicker.setHelperText("Must be within 7 days from today");
        datePicker.setRequiredIndicatorVisible(true);
        datePicker.addClassNames(Margin.Bottom.SMALL);

        Div routeSelectionSection = new Div();
        routeSelectionSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        routeDep = new ComboBox<>("From");
        routeDep.setRequiredIndicatorVisible(true);
        routeDep.setItems(fromStations);
        routeDep.setVisible(true);
        routeDep.setEnabled(false);
        routeDep.addClassNames(Margin.Bottom.SMALL);

        routeArr = new ComboBox<>("To");
        routeArr.setRequiredIndicatorVisible(true);
        routeArr.setVisible(true);
        routeArr.setEnabled(false);
        routeArr.addClassNames(Margin.Bottom.SMALL);

        routeSelectionSection.add(routeDep, routeArr);

        trainSelect = new ComboBox<>("Train");
        trainSelect.setRequiredIndicatorVisible(true);
        trainSelect.setVisible(true);
        trainSelect.setEnabled(false);
        trainSelect.addClassNames(Margin.Bottom.SMALL);

        Div subSection = new Div();
        subSection.addClassNames(Display.FLEX, FlexWrap.WRAP, Gap.MEDIUM);

        wagonNumber = new ComboBox<>("Wagon");
        wagonNumber.setRequiredIndicatorVisible(true);
        wagonNumber.setItems("1", "2", "3", "4", "5", "6");
        wagonNumber.setVisible(true);
        wagonNumber.setEnabled(false);
        wagonNumber.addClassNames(Margin.Bottom.SMALL);

        seatNumber = new ComboBox<>("Seat");
        seatNumber.setRequiredIndicatorVisible(true);
        seatNumber.setItems(seatNumbers);
        seatNumber.setVisible(true);
        seatNumber.setEnabled(false);
        seatNumber.addClassNames(Margin.Bottom.SMALL);

        subSection.add(wagonNumber, seatNumber);

        ticketPrice = new Paragraph("");
        ticketPrice.setVisible(false);
        ticketPrice.addClassNames(Margin.Top.MEDIUM, FontSize.MEDIUM, TextColor.SECONDARY);

//EVENTS
        datePicker.addValueChangeListener(e -> {
            updateStations();
            routeDep.setEnabled(true);
        });


        routeDep.addValueChangeListener(e -> {
          routeArr.setEnabled(true);
        });

        routeArr.addValueChangeListener(e -> {

            if(routeDep.getValue().equals(routeArr.getValue())){
                routeArr.setValue("");
            }
            else{
                trainSelect.setEnabled(true);
                updateTrains(datePicker.getValue());
            }

            //updateTicketDetails(trainSelect.getValue(), routeDep.getValue(), routeArr.getValue(), wagonNumber.getValue(), seatNumber.getValue(), ticketPrice.getText(),user.getUsername(), user.getEmail(), review_code);

        });

        trainSelect.addValueChangeListener(e -> {
            //updateStations(trainSelect.getValue());
        });

        wagonNumber.addValueChangeListener(e -> {
        });

        seatNumber.addValueChangeListener(e -> {
            Optional<Train> t = trainService.get(trainSelect.getValue().split(" ")[0]);
            float price = ticketRepository.getTicketPrice(t.get().getId(), routeDep.getValue().split(" \\[")[0], routeArr.getValue().split(" \\[")[0], Integer.valueOf(wagonNumber.getValue()));
            ticketPrice.setVisible(true); updatePrice(ticketPrice, price);
            updateTicketDetails(trainSelect.getValue(), routeDep.getValue(), routeArr.getValue(), wagonNumber.getValue(), seatNumber.getValue(), ticketPrice.getText(), user.getUsername(), user.getEmail(), review_code);
        });

        shippingDetails.add(header, datePicker, routeSelectionSection, trainSelect, subSection, ticketPrice);
        return shippingDetails;
    }

    private void updateArrStations(String trainID, String depID){
        Optional<Train> t = trainService.get(trainID.split(" ")[0]);
        //Optional<Station> st = stationService.get(depID.split(" \\[")[0]);
        toStations.clear();
        for(Station s : stationService.getAfterStations(t.get().getId(), depID.split(" \\[")[0])){
            toStations.add(s.getStationName() + " [" + Utils.formatTime(s.getArrTime()) + "]");
        }
        routeArr.setItems(toStations);
    }

    private void updateStations(){
            fromStations.clear();
            toStations.clear();
            for(String st : stationService.stationList()){
                fromStations.add(st);
                toStations.add(st);
            }
            routeDep.setItems(fromStations);
            routeArr.setItems(toStations);
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
                Optional<Train> t = trainService.get(trainSelect.getValue().split(" ")[0]);
                ticketRepository.save(new Ticket(user.getId(), t.get().getId(), Date.valueOf(datePicker.getValue()), routeDep.getValue().split(" \\[")[0], routeArr.getValue().split(" \\[")[0], wagonNumber.getValue(), seatNumber.getValue(), review_code));
                n.setDuration(4000);
                n.setPosition(Notification.Position.BOTTOM_START);
                n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                Thread inputThread = new Thread(() -> {
                    try {
                        emailService.sendTicketDetails(email.getValue(), ticketDetails);
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
        ticketDetails[6] = details[6];
        ticketDetails[7] = details[7];
        ticketDetails[8] = details[8];
    }


    public void updatePrice(Paragraph p, float value){
        p.setText("Total " + value + " lei.");
    }

    public void updateTrains(LocalDate date){
        trains.clear();
        //data de azi
        Date nowDate = new Date(System.currentTimeMillis());
        Date selectedDate = Date.valueOf(date);

        Calendar nowCalendar = Calendar.getInstance();
        Calendar selectedDateCalendar = Calendar.getInstance();

        nowCalendar.setTime(nowDate);
        selectedDateCalendar.setTime(selectedDate);
        for(Train t : trainService.trainList(routeDep.getValue(), routeArr.getValue())){

            if(nowCalendar.get(Calendar.DAY_OF_MONTH) == selectedDateCalendar.get(Calendar.DAY_OF_MONTH)) {
                Calendar trainArrivalCalendar = Calendar.getInstance();
                trainArrivalCalendar.setTimeInMillis(t.getArrTime()*1000);

                int trainH = trainArrivalCalendar.get(Calendar.HOUR_OF_DAY);
                int nowH = nowCalendar.get(Calendar.HOUR_OF_DAY);
                int trainM = trainArrivalCalendar.get(Calendar.MINUTE);
                int nowM = nowCalendar.get(Calendar.MINUTE);

                if((trainH > nowH || (trainH == nowH && trainM > nowM))){
                    trains.add(t.getTrainName() + " [" + t.getDepStation() + " -> " + t.getArrStation() + "]");
                }
            } else {
                trains.add(t.getTrainName() + " [" + t.getDepStation() + " -> " + t.getArrStation() + "]");
            }
        }
        trainSelect.setItems(trains);
    }
}