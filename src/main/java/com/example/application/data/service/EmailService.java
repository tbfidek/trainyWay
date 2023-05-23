package com.example.application.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTemporaryPasswordEmail(String recipientEmail, String temporaryPassword) {
        String subject = "Temporary Password";
        String content = "Your temporary password is: " + temporaryPassword;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
}

