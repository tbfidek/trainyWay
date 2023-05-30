package com.example.application;

import com.example.application.data.entity.Train;
import com.example.application.data.service.TrainRepository;
import com.example.application.data.service.TrainService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@EnableScheduling
@Theme(value = "trainSched")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
