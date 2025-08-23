package lk.ijse.raillankaprobackend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Override
    public void sendStationMasterCredentials(String template, StaffDto staffDto , String id) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("rail.lanka.pro@gmail.com");
            helper.setTo(staffDto.getEmail());
            helper.setSubject("Your RailLanka Pro Staff Account Credentials");

            Context context = new Context();
            context.setVariable("staffName", staffDto.getFirstname() + " " + staffDto.getLastname());
            context.setVariable("id", id);
            context.setVariable("username", staffDto.getUserName());
            context.setVariable("password", staffDto.getPassword());
            context.setVariable("station", staffDto.getRailwayStation());
            context.setVariable("role", "Station Master");

            String htmlContent = templateEngine.process(template, context);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendCounterCredentials(String template, CounterDto counterDto, String id) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("rail.lanka.pro@gmail.com");
            helper.setTo(counterDto.getEmail());
            helper.setSubject("Your RailLanka Pro Staff Account Credentials");

            Context context = new Context();
            context.setVariable("staffName", counterDto.getFirstname() + " " + counterDto.getLastname());
            context.setVariable("id", id);
            context.setVariable("username", counterDto.getUserName());
            context.setVariable("password", counterDto.getPassword());
            context.setVariable("station", counterDto.getRailwayStation());
            context.setVariable("counter", counterDto.getCounterNumber());
            context.setVariable("role", "Counter");

            String htmlContent = templateEngine.process(template, context);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }

    }
}
