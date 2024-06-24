package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/email", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(maxAge = 3600)
public class EmailingController {

    private final AdServiceImpl adService;
    private final UserServiceImpl userService;

    public EmailingController(AdServiceImpl adService, UserServiceImpl userService) {
        this.adService = adService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public void sendEmail() {
        //TODO implement
    }
}
