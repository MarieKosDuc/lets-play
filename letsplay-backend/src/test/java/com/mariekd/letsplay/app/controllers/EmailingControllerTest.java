package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.request.ContactEmailRequest;
import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmailingControllerTest {

    @InjectMocks
    private EmailingController emailingController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AdServiceImpl adService;

    @Mock
    private EmailService emailService;

    @Captor
    ArgumentCaptor<String> emailCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendContactEmailSuccessfully() throws MessagingException {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");

        Ad ad = new Ad();
        ad.setId(1);
        ad.setTitle("Test Ad");
        ad.setPostedBy(new User());

        ContactEmailRequest contactEmailRequest = new ContactEmailRequest("1",
                user.getId().toString(), "Test message");


        when(adService.getAdById(anyInt())).thenReturn(Optional.of(ad));
        when(userService.getUserById(any(UUID.class))).thenReturn(user);

        ResponseEntity<Object> response = emailingController.sendContactEmail(contactEmailRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("{message=Email sent successfully}", response.getBody().toString() );

        verify(emailService, times(1)).sendContactEmail(emailCaptor.capture(), emailCaptor.capture(), emailCaptor.capture(), emailCaptor.capture(), emailCaptor.capture(), emailCaptor.capture());

        List<String> actualEmailContent = emailCaptor.getAllValues();

        assertEquals(6, actualEmailContent.size());
        assertEquals(null, actualEmailContent.get(0));
        assertEquals("Nouveau message pour ton annonce Test Ad", actualEmailContent.get(1));
        assertEquals("Hello, null! Tu as reçu un nouveau message pour ton annonce Test Ad!", actualEmailContent.get(2));
        assertEquals("Test User", actualEmailContent.get(3));
        assertEquals("test@example.com", actualEmailContent.get(4));
        assertEquals("Test message", actualEmailContent.get(5));
    }

    @Test
    public void sendContactEmailFails() throws MessagingException {
        ContactEmailRequest contactEmailRequest = new ContactEmailRequest("1", UUID.randomUUID().toString(), "Test message");

        when(adService.getAdById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = emailingController.sendContactEmail(contactEmailRequest);

        assertEquals(500, response.getStatusCodeValue());
        verify(emailService, times(0)).sendContactEmail(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
