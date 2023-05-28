package com.example.application.data.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

import java.io.IOException;


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
    public void sendTicketDetails(String recipientEmail, String... details) throws IOException, MessagingException {
          MimeMessage message = javaMailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(message, true);

         //Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Prepare the ticket details content
        StringBuilder content = new StringBuilder();
        content.append("Details about your ticket:<br>")
                .append("Train: ").append(details[0]).append("<br>")
                .append("From: ").append(details[1]).append("<br>")
                .append("To: ").append(details[2]).append("<br>")
                .append("Wagon: ").append(details[3]).append("<br>")
                .append("Seat: ").append(details[4]).append("<br>")
                .append("Price: ").append(details[5]);

        // Add the ticket details content to the PDF
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 700);
        contentStream.showText(content.toString());
        contentStream.endText();
        contentStream.close();

        // Prepare the PDF document for sending via email
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        byte[] pdfBytes = baos.toByteArray();
        helper.setTo(recipientEmail);
        helper.setFrom("trainyway@firemail.cc");
        helper.setSubject("Ticket details");
        helper.setText("Please find attached the ticket details in PDF format.");

        // Attach the PDF document to the email
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        helper.addAttachment("ticket_details.pdf", resource);

//        // Send the email
        javaMailSender.send(message);
    }
}

