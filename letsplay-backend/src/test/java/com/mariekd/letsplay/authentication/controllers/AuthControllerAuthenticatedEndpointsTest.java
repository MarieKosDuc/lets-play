package com.mariekd.letsplay.authentication.controllers;

import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.payload.request.UserUpdateRequest;
import com.mariekd.letsplay.authentication.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * Test class for the AuthController REST controller.
 * Utilizes Spring Boot's testing support to simulate HTTP requests and assert responses.
 * Tests endpoints for which no a specific user is needed.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetails validUserDetails;

    @BeforeEach
    void setUp() {
        validUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("validUser")
                .password("password")
                .roles("USER")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(validUserDetails, null, validUserDetails.getAuthorities())
        );
    }

    /**
     * Test the POST /api/users/password/{id} endpoint for password update
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void updatePassword_ReturnsOk_WhenUserIsModifyingTheirOwnPassword() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);

        mockMvc.perform(put("/api/users/password/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer fake-jwt-token")
                        .content("newPassword")
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void updatePassword_ReturnsUnauthorized_WhenUserIsNotProfileOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);

        UUID otherUserId = UUID.randomUUID();

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);

        mockMvc.perform(put("/api/users/password/{id}", otherUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer fake-jwt-token")
                        .content("newPassword")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test the POST /api/users/{id} endpoint to update a user
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    public void updateUser_ReturnsOk_WhenUserIsAuthorized() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated User", "Updated Picture");
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(userService.updateUser(any(UUID.class), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void updateUser_ReturnsUnauthorized_WhenUserIsNotAuthorized() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID unauthorizedUserId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);

        UserUpdateRequest updateRequest = new UserUpdateRequest("Unauthorized User", "Unauthorized Picture");
        String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);

        mockMvc.perform(put("/api/users/" + unauthorizedUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isUnauthorized());
    }
}