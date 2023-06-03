package com.example.application.data.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    private UserRepository userRepository;

    public EmailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void sendTemporaryPasswordEmail(String recipientEmail) {
        String subject = "Temporary Password";
        String resetCode = RandomStringUtils.randomAlphabetic(10);
        String content = "link: http://localhost:6969/resetpassword/" + resetCode;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setFrom("trainyway@firemail.cc");
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
        userRepository.createResetQuery(recipientEmail, resetCode);
    }
    public void sendTicketDetails(String recipientEmail, String... details) throws IOException, MessagingException {
          MimeMessage message = javaMailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(message, true);

         //Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Add the ticket details content to the PDF
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        PDFont font = PDType0Font.load(document, new File("src/main/resources/Roboto-Thin.ttf"));
        contentStream.setFont(font, 12);
        contentStream.beginText();
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(25, 725);
        contentStream.showText("Details about your ticket: ");
        contentStream.newLine();
        contentStream.newLine();
        contentStream.showText("Name: " + details[6]);
        contentStream.newLine();
        contentStream.showText("Email: " + details[7]);
        contentStream.newLine();
        contentStream.showText("Train: "+ details[0]);
        contentStream.newLine();
        contentStream.showText("From: "+details[1]);
        contentStream.newLine();
        contentStream.showText("To: "+ details[2]);
        contentStream.newLine();
        contentStream.showText("Wagon: "+details[3]);
        contentStream.newLine();
        contentStream.showText("Seat: "+details[4]);
        contentStream.newLine();
        contentStream.showText("Price: "+ details[5].replace("Total ", ""));
        contentStream.newLine();
        contentStream.showText("Review code: "+ details[8]);
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

