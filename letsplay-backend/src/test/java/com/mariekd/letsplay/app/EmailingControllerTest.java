package com.mariekd.letsplay.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.entities.AdBuilder;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.app.services.EmailService;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class EmailingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AdService adService;

    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void sendContactEmail_ReturnsOkIfEmailSent() throws Exception {
        User sendingTo = new User();
        sendingTo.setEmail("sendingto@mail.com");
        sendingTo.setName("Ad Author");
        sendingTo.setId(UUID.randomUUID());

        User testUser = new User();
        testUser.setEmail("sendingfrom@mail.com");
        testUser.setName("Test User");
        testUser.setId(UUID.randomUUID());

        Ad testAd = new AdBuilder().setId(1)
                .setPostedBy(sendingTo)
                .setTitle("Test Ad")
                .build();

        String contactEmailRequestJson = String.format("{ \"adId\": \"1\", \"fromUser\": \"%s\", \"messageContent\": \"Test message\" }", testUser.getId().toString());
        when(adService.getAdById(1)).thenReturn(Optional.ofNullable(testAd));
        when(userService.getUserById(UUID.fromString(testUser.getId().toString()))).thenReturn(testUser);
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactEmailRequestJson))
                .andExpect(status().isOk());

            verify(emailService).sendContactEmail(
                    any(String.class),
                    any(String.class),
                    any(String.class),
                    eq(testUser.getName()),
                    eq(testUser.getEmail()),
                    eq("Test message")
            );
        }

    @Test
    @WithMockUser(username = "testUser")
    public void sendContactEmail_ReturnsInternalServerErrorIfEmailNotSent() throws Exception {
        String contactEmailRequestJson = "{ \"adId\": \"1\", \"fromUser\": \"testUser\", \"messageContent\": \"Test message\" }";

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactEmailRequestJson))
                .andExpect(status().isInternalServerError());
    }

}