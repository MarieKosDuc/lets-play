package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.dto.ContactEmailDTO;
import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
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
    private final UserServiceImpl userService;
    private final AdServiceImpl adService;
    private final EmailService emailService;

    public EmailingController(AdServiceImpl adService, UserServiceImpl userService, EmailService emailService) {
        this.userService = userService;
        this.adService = adService;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Object> sendContactEmail(@RequestBody ContactEmailDTO contactEmailDTO) {
        try {
            Ad contactAd = adService.getAdById(Integer.parseInt(contactEmailDTO.getAdId())).get();

            User sendingTo = contactAd.getPostedBy();
            User sendingFrom = userService.getUserById(UUID.fromString(contactEmailDTO.getFromUser()));

            String to = sendingTo.getEmail();
            String subject = "Nouveau message pour ton annonce " + contactAd.getTitle();
            String greetingText = "Hello, " + sendingTo.getName() + "! Tu as reçu un nouveau message pour ton annonce " + contactAd.getTitle() + "!";
            String userSenderName = sendingFrom.getName();
            String userSenderEmail = sendingFrom.getEmail();

            emailService.sendContactEmail(to, subject, greetingText, userSenderName, userSenderEmail, contactEmailDTO.getMessageContent());

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
