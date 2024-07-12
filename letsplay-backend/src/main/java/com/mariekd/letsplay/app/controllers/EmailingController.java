package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.request.ContactEmailRequest;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value="/api/contact", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(maxAge = 3600)
public class EmailingController {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EmailingController.class);
    private final UserService userService;
    private final AdService adService;
    private final EmailService emailService;

    public EmailingController(AdService adService, UserService userService, EmailService emailService) {
        this.userService = userService;
        this.adService = adService;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Map<String, String>> sendContactEmail(@RequestBody ContactEmailRequest contactEmailRequest) {
        try {
            Ad contactAd = adService.getAdById(Integer.parseInt(contactEmailRequest.adId())).get();

            User sendingTo = contactAd.getPostedBy();
            User sendingFrom = userService.getUserById(UUID.fromString(contactEmailRequest.fromUser()));

            String to = sendingTo.getEmail();
            String subject = "Nouveau message pour ton annonce " + contactAd.getTitle();
            String greetingText = "Hello, " + sendingTo.getName() + "! Tu as reçu un nouveau message pour ton annonce " + contactAd.getTitle() + "!";
            String userSenderName = sendingFrom.getName();
            String userSenderEmail = sendingFrom.getEmail();

            emailService.sendContactEmail(to, subject, greetingText, userSenderName, userSenderEmail, contactEmailRequest.messageContent());

            Map<String, String> okResponse = new HashMap<>();
            okResponse.put("message","Email sent successfully");

            return new ResponseEntity<>(okResponse, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error while sending email: {}", e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
