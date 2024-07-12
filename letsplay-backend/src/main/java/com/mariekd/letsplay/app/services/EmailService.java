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

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendConfirmEmail(String to, String subject, String h1, String text, String link, String linkText) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress("no-reply@letsplay-metal.fr"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);

        String htmlContent = "<h1>" + h1 + "</h1>" +
                "<p>" + text + "</p>"
                + "<a href=\"" + link + "\">" + linkText + "</a>"
                + "<p>Musicalement, <br> L'équipe Let's Play</p>";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        javaMailSender.send(message);
    }

    public void sendContactEmail(String to, String subject, String greetingText, String userSenderName,
                                 String userSenderEmail, String messageContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress("no-reply@letsplay-metal.fr"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);

        String htmlContent = "<h1>" + greetingText + "</h1>"
                + "<h2>Ce message t'est envoyé par : <a href=\"mailto:" + userSenderEmail + "\">" + userSenderName + "</a> </h2>"
                + "<div>\""+ messageContent +"\"</div>"
                + "<br><div>Si tu souhaites répondre à ce message, n'hésite pas à recontacter cette personne en utilisant son adresse ci-dessus.</div>"
                + "<div>Nous garantissons que <strong>toutes les adresses email de nos utilisateurs sont vérifiées.</strong> </div>"
                + "<div><br>Let's Play ne diffuse jamais ton adresse. Tant que tu ne l'auras pas recontacté, cet utilisateur ne peut pas te joindre directement.</div>"
                + "<div><br>Si tu reçois un message suspect, n'hésite pas à nous contacter.</div>"
                + "<p>Musicalement, <br> L'équipe Let's Play</p>";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        javaMailSender.send(message);
    }

}
