package com.example.cleonoraadmin.service.imp;

import com.example.cleonoraadmin.service.MailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    private final TemplateEngine templateEngine;
    private final SendGrid sendGrid;

    @Value("${sender}")
    private String sender;

    public MailServiceImpl(TemplateEngine templateEngine, SendGrid grid) {
        this.templateEngine = templateEngine;
        this.sendGrid = grid;
    }

    @Async
    @Override
    public void sendToken(String token, String to, HttpServletRequest httpRequest) {
        log.info("sendToken() - Sending token to {}", to);
        Email from = new Email(sender);
        String subject = "Встановлення нового паролю";
        Email toEmail = new Email(to);
        Content content = new Content("text/html", build(token,httpRequest));
        Mail mail = new Mail(from, subject, toEmail, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
            log.info("sendToken() - Token was sent");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    private String build(String token, HttpServletRequest httpRequest) {
        Context context = new Context();
        final String fullUrl = ServletUriComponentsBuilder.fromRequestUri(httpRequest).build().toUriString();
        log.info("url: {}", fullUrl);
        int in = fullUrl.lastIndexOf("/");
        String baseUrl = fullUrl.substring(0,in);
        String l = baseUrl +"/changePassword?token="+token;
        context.setVariable("link", l);
        return templateEngine.process("email/emailTemplate", context);
    }
}
