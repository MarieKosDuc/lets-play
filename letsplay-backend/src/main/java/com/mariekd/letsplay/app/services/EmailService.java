package com.mariekd.letsplay.app.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@letsplay-metal.fr");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);

        LOGGER.info("Email sent to {} with subject: {}", to, subject);
    }

    public void sendHtmlEmail(String to, String subject, String h1, String text, String link) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress("no-reply@letsplay-metal.fr"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);

        String htmlContent = "<h1>" + h1 + "</h1>" +
                "<p>" + text + "</p>"
                + "<a href=\"" + link + "\">" + link + "</a>"
                + "<p>Musicalement, <br> L'équipe Let's Play</p>";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        javaMailSender.send(message);
    }

    public void sendContactEmail(String userSenderEmail,
                                 String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress("no-reply@letsplay-metal.fr"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);

        String htmlContent = "<p>" + text + "</p>"
                + "<p>Ce message t'es envoyé par : <a href=mailto" + userSenderEmail + "></a> </p>"
                + "<p>Musicalement, <br> L'équipe Let's Play</p>";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        javaMailSender.send(message);
    }
}
