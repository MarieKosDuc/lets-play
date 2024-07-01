package com.mariekd.letsplay.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariekd.letsplay.authentication.entities.RefreshToken;
import com.mariekd.letsplay.authentication.entities.ResetPasswordToken;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.jwt.JwtService;
import com.mariekd.letsplay.authentication.models.UserInfo;
import com.mariekd.letsplay.authentication.payload.request.LoginRequest;
import com.mariekd.letsplay.authentication.payload.request.TokenRefreshRequest;
import com.mariekd.letsplay.authentication.payload.response.LoginOkResponse;
import com.mariekd.letsplay.authentication.services.RefreshTokenService;
import com.mariekd.letsplay.authentication.services.UserService;
import com.mariekd.letsplay.authentication.services.ResetPasswordTokenService;
import com.mariekd.letsplay.app.services.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class contains integration tests for the authentication controller.
 * It tests the behavior of the controller when the user is not logged in.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTestNotLoggedInMethods {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @MockBean
    private ResetPasswordTokenService resetPasswordTokenService;

    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(roles = {"USER"})
    public void getUser_ReturnsUserInfoResponse() throws Exception {
        UUID testUserId = UUID.randomUUID();
        User testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setProfilePicture("Test Picture");

        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/api/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserId.toString())))
                .andExpect(jsonPath("$.username", is("Test User")))
                .andExpect(jsonPath("$.profilePicture", is("Test Picture")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void getUser_ReturnsUserInfoResponseForAdmin() throws Exception {
        UUID testUserId = UUID.randomUUID();
        User testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setProfilePicture("Test Picture");

        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/api/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserId.toString())))
                .andExpect(jsonPath("$.username", is("Test User")))
                .andExpect(jsonPath("$.profilePicture", is("Test Picture")));
    }

    @Test
    public void getUser_FailsIfNotAuthenticated() throws Exception {
        UUID testUserId = UUID.randomUUID();
        User testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setProfilePicture("Test Picture");

        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/api/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    //TODO : écrire test pour le login

    @Test
    public void refreshToken_ReturnsOkWhenTokenIsValid() throws Exception {
        // Arrange
        String testToken = "testToken";
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest(testToken);
        String tokenRefreshRequestJson = objectMapper.writeValueAsString(tokenRefreshRequest);

        User testUser = new User();
        testUser.setEmail("testUser@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(testUser);
        refreshToken.setToken(testToken);
        Optional<RefreshToken> optionalRefreshToken = Optional.of(refreshToken);

        when(jwtService.getJwtFromCookies(any())).thenReturn("testJwt");
        when(jwtService.getUserNameFromJwtToken(any())).thenReturn(testUser.getEmail());
        when(userService.getUserByEmail(any())).thenReturn(testUser);
        when(refreshTokenService.findByToken(testToken)).thenReturn(optionalRefreshToken);
        when(refreshTokenService.findByToken(tokenRefreshRequestJson)).thenReturn(optionalRefreshToken);
        Optional<RefreshToken> test = refreshTokenService.findByToken(testToken);
        when(jwtService.generateJwtToken(any())).thenReturn("newJwt");

        mockMvc.perform(post("/api/users/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenRefreshRequestJson))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "newJwt"))
                .andExpect(jsonPath("$.token", is(testToken)));
    }




    @Test
    public void forgotPassword_ReturnsOkIfEmailSent() throws Exception {
        String passwordResetRequestJson = "{ \"email\": \"testUser@example.com\" }";

        User testUser = new User();
        testUser.setEmail("testUser@example.com");
        when(userService.getUserByEmail("testUser@example.com")).thenReturn(testUser);
        when(resetPasswordTokenService.createResetPasswordToken(any(User.class))).thenReturn(new ResetPasswordToken());

        mockMvc.perform(post("/api/users/resetpassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordResetRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void forgotPassword_ReturnsErrorIfEmailNotFound() throws Exception {
        String passwordResetRequestJson = "{ \"email\": \"testUser@example.com\" }";

        mockMvc.perform(post("/api/users/resetpassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordResetRequestJson))
                .andExpect(status().isInternalServerError());
    }
}