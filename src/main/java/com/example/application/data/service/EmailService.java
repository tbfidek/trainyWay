package com.example.application.data.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTemporaryPasswordEmail(String recipientEmail) {
        String subject = "Temporary Password";
        String content = "Your temporary password is: " + RandomStringUtils.randomAlphabetic(10);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setFrom("trainyway@firemail.cc");
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    public void sendTicketDetails(String recipientEmail, String... details){
        String subject = "Ticket details";
        String content = "Details about your ticket:\nTrain: " + details[0] + "\nFrom: " + details[1] + "\nTo: " + details[2] +
                "\n Wagon: " + details[3] + "\n Seat: " + details[4] + "\nPrice: " + details[5];
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setFrom("trainyway@firemail.cc");
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
}

