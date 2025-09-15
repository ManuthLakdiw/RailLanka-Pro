package lk.ijse.raillankaprobackend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lk.ijse.raillankaprobackend.dto.CounterDto;
import lk.ijse.raillankaprobackend.dto.CustomerSupportDto;
import lk.ijse.raillankaprobackend.dto.StaffDto;
import lk.ijse.raillankaprobackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

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

    @Override
    public void sendOtpCode(String toEmail, String otp) {
        try {
            Context context = new Context();
            context.setVariable("code", otp);

            String process = templateEngine.process("verification-code-email", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setSubject("RailLanka Pro - Password Reset Verification Code");
            helper.setFrom("rail.lanka.pro@gmail.com");
            helper.setTo(toEmail);
            helper.setText(process, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendCustomerSupportEmail(CustomerSupportDto customerSupportDto) {
        try {
            MimeMessage messageForTeam = mailSender.createMimeMessage();
            MimeMessageHelper helperForTeam = new MimeMessageHelper(messageForTeam, true);

            helperForTeam.setFrom("rail.lanka.pro@gmail.com");
            helperForTeam.setTo("itsraillanka2025@gmail.com");
            helperForTeam.setSubject("IT Support Ticket Received: " + customerSupportDto.getTicketId());

            Context context = new Context();
            context.setVariable("ticketId", customerSupportDto.getTicketId());
            context.setVariable("requesterName", customerSupportDto.getRequesterName());
            context.setVariable("requesterEmail", customerSupportDto.getRequesterEmail());
            context.setVariable("subject", customerSupportDto.getSubject());
            context.setVariable("description", customerSupportDto.getDescription());
            context.setVariable("category", customerSupportDto.getCategory());
            context.setVariable("priority", customerSupportDto.getPriority());
            context.setVariable("submittedDate", LocalDate.now());
            if (customerSupportDto.getAttachments() != null) {
                context.setVariable("attachments", "Please note that an attachment is included with this email.check Below!");

            }else{
                context.setVariable("attachments", "No Attachments Includes");

            }

            String htmlContent = templateEngine.process("it-support-email", context);

            helperForTeam.setText(htmlContent, true);
            helperForTeam.addInline("logo", new ClassPathResource("images/logo.png"));


            if (customerSupportDto.getAttachments() != null) {
                for (MultipartFile file : customerSupportDto.getAttachments()) {
                    helperForTeam.addAttachment(
                            file.getOriginalFilename(),
                            new ByteArrayResource(file.getBytes())
                    );
                }
            }


            mailSender.send(messageForTeam);

            MimeMessage customerMessage = mailSender.createMimeMessage();
            MimeMessageHelper customerHelper = new MimeMessageHelper(customerMessage, true);
            customerHelper.setFrom("rail.lanka.pro@gmail.com");
            customerHelper.setTo(customerSupportDto.getRequesterEmail());
            customerHelper.setSubject("Support Request Confirmation: " + customerSupportDto.getTicketId());

            // Customer confirmation email template
            Context customerContext = new Context();
            customerContext.setVariable("ticketId", customerSupportDto.getTicketId());
            customerContext.setVariable("requesterName", customerSupportDto.getRequesterName());
            customerContext.setVariable("requesterEmail", customerSupportDto.getRequesterEmail());
            customerContext.setVariable("category", customerSupportDto.getCategory());
            customerContext.setVariable("description", customerSupportDto.getDescription());
            customerContext.setVariable("priority", customerSupportDto.getPriority());
            customerContext.setVariable("submittedDate", LocalDate.now());

            String customerHtmlContent = templateEngine.process("client-support-confirmation-email", customerContext);
            customerHelper.setText(customerHtmlContent, true);

            customerHelper.addInline("logo", new ClassPathResource("images/logo.png"));

            mailSender.send(customerMessage);



        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send support ticket email: " + e.getMessage(), e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
