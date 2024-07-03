package com.mariekd.letsplay.authentication.controllers;


import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllUsers_ReturnsUsersList() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setProfilePicture("test.jpg");

        Role adminRole = new Role("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].profilePicture", is(user.getProfilePicture())));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user")
    public void getAllUsers_ReturnsForbiddenIfUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteUserByAdmin_ReturnsOkIfUserDeleted() throws Exception {
        UUID userId = UUID.randomUUID();
        mockMvc.perform(delete("/api/admin/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user")
    public void deleteUserByAdmin_ReturnsForbiddenIfUserIsNotAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        mockMvc.perform(delete("/api/admin/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteAdByAdmin_ReturnsOkIfAdDeleted() throws Exception {
        int adId = 1;
        mockMvc.perform(delete("/api/admin/ads/" + adId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user")
    public void deleteAdByAdmin_ReturnsForbiddenIfUserIsNotAdmin() throws Exception {
        int adId = 1;
        mockMvc.perform(delete("/api/admin/ads/" + adId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}