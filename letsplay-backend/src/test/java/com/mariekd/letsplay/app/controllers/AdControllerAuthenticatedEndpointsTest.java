package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.entities.*;
import com.mariekd.letsplay.app.request.CreateAdRequest;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.app.services.LocationService;
import com.mariekd.letsplay.app.services.MusicianTypeService;
import com.mariekd.letsplay.app.services.StyleService;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AdController REST controller.
 * Utilizes Spring Boot's testing support to simulate HTTP requests and assert responses.
 * Tests endpoints for which authentication with a specific user is required.
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AdControllerIntegrationTest {

    @Autowired
    private ObjectMapper om;

    @MockBean
    private AdService adService;

    @MockBean
    private UserService userService;

    @MockBean
    private MusicianTypeService musicianTypeService;

    @MockBean
    private StyleService styleService;

    @MockBean
    private LocationService locationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetails validUserDetails;
    private UUID testUuid;
    private User testUser;

    private Style style = new Style(1, "test style");
    private String[] styles = {style.getName()};
    private Location location = new Location(1, "test location", new HashSet<>());
    private MusicianType musicianType1 = new MusicianType(1, "test musician type", new HashSet<>());
    private MusicianType musicianType2 = new MusicianType(2, "test musician type 2", new HashSet<>());

    private Ad testAd;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testUser = new User(testUuid, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);

        testAd = new AdBuilder()
                .setId(1)
                .setPostedBy(testUser)
                .setTitle("Initial Test Ad")
                .setDescription("Test description")
                .setLocation(location)
                .setFrom(musicianType1)
                .setSearching(musicianType2)
                .setCreatedAt(Instant.now())
                .setStyles(new HashSet<>(Arrays.asList(style)))
                .build();

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
     * Test the create ad endpoint.
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void createAd_ReturnsOk_WhenAdIsCreated() throws Exception {
       CreateAdRequest createAdRequest = new CreateAdRequest("Test Ad", musicianType1.getName(), musicianType2.getName(), "band.jpg", styles, location.getName(), "test description");

        Mockito.when(userService.getUserById(testUuid)).thenReturn(testUser);
        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(testUser);
        Mockito.when(adService.getAdByTitle(createAdRequest.title())).thenReturn(Optional.empty());
        Mockito.when(musicianTypeService.findByName(musicianType1.getName())).thenReturn(musicianType1);
        Mockito.when(musicianTypeService.findByName(musicianType2.getName())).thenReturn(musicianType2);
        Mockito.when(styleService.findByName(style.getName())).thenReturn(style);
        Mockito.when(locationService.findByName(location.getName())).thenReturn(location);

        mockMvc.perform(post("/api/ads/create")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-jwt-token")
                                .content(objectMapper.writeValueAsString(createAdRequest))
                                .contentType(APPLICATION_JSON)
                        )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void createAd_ReturnsBadRequest_WhenAdAlreadyExists() throws Exception {
        CreateAdRequest createAdRequest = new CreateAdRequest("Test Ad", musicianType1.getName(), musicianType2.getName(), "band.jpg", styles, location.getName(), "test description");

        Mockito.when(userService.getUserById(testUuid)).thenReturn(testUser);
        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(testUser);
        Mockito.when(adService.getAdByTitle(createAdRequest.title())).thenReturn(Optional.of(new Ad()));


        mockMvc.perform(post("/api/ads/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer fake-jwt-token")
                        .content(objectMapper.writeValueAsString(createAdRequest))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the update ad endpoint and the boolean isUserAdAuthor method.
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void updateAdByUser_ReturnsOk_WhenUserIsAdAuthor() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);
        int adId = 1;

        CreateAdRequest updateAdRequest = new CreateAdRequest("Updated Ad", musicianType1.getName(), musicianType2.getName(), "band.jpg", styles, location.getName(), "Updated test description");
        String updateAdRequestJson = objectMapper.writeValueAsString(updateAdRequest);

        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);
        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.of(testAd));
        Mockito.when(musicianTypeService.findByName(musicianType1.getName())).thenReturn(musicianType1);
        Mockito.when(musicianTypeService.findByName(musicianType2.getName())).thenReturn(musicianType2);
        Mockito.when(styleService.findByName(style.getName())).thenReturn(style);
        Mockito.when(locationService.findByName(location.getName())).thenReturn(location);

        mockMvc.perform(put("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON)
                        .content(updateAdRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "otherUser", roles = {"USER"})
    void updateAdByUser_ReturnsUnauthorized_WhenUserIsNotAdAuthor() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "otherUser", "otherUser@example.com", "password", "profilePicture", true, null, null, null);
        int adId = 1;

        CreateAdRequest updateAdRequest = new CreateAdRequest("Updated Ad", musicianType1.getName(), musicianType2.getName(), "band.jpg", styles, location.getName(), "Updated test description");
        String updateAdRequestJson = objectMapper.writeValueAsString(updateAdRequest);

        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);
        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.of(testAd));

        mockMvc.perform(put("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON)
                        .content(updateAdRequestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "otherUser", roles = {"USER"})
    void updateAdByUser_ReturnsBadRequest_WhenAdIsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "otherUser", "otherUser@example.com", "password", "profilePicture", true, null, null, null);
        int adId = 1;

        CreateAdRequest updateAdRequest = new CreateAdRequest("Updated Ad", musicianType1.getName(), musicianType2.getName(), "band.jpg", styles, location.getName(), "Updated test description");
        String updateAdRequestJson = objectMapper.writeValueAsString(updateAdRequest);

        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);
        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON)
                        .content(updateAdRequestJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the delete ad endpoint and the isUserAdAuthor method.
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void deleteAdByUser_ReturnsOk_WhenUserIsAdAuthorAndAdExists() throws Exception {
        int adId = 1;

        User user = new User(UUID.randomUUID(), "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);
        Ad ad = new Ad();
        ad.setId(adId);
        ad.setPostedBy(user);

        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.of(ad));

        mockMvc.perform(delete("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void deleteAdByUser_ReturnsBadRequest_WhenAdIsNotFound() throws Exception {
        int adId = 999;

        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "otherUser", roles = {"USER"})
    void deleteAdByUser_ReturnsUnauthorized_WhenUserIsNotAdAuthor() throws Exception {
        int adId = 1;

        User user = new User(UUID.randomUUID(), "otherUser", "otherUser@example.com", "password", "profilePicture", true, null, null, null);
        Ad ad = new Ad();
        ad.setId(adId);
        ad.setPostedBy(new User(UUID.randomUUID(), "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null)); // Different user

        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.of(ad));
        Mockito.when(userService.getUserFromRequest(Mockito.any())).thenReturn(user);

        mockMvc.perform(delete("/api/ads/{id}", adId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test the GET /favorites/{id} endpoint.
     * @throws Exception
     */

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void getFavoriteAds_ReturnsAdsInReverseOrder_WhenLikedAdsExist() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);
        Ad olderAd = new AdBuilder().setId(1).setCreatedAt(Instant.now().minusSeconds(600))
                .setPostedBy(testUser)
                .setTitle("Initial Test Ad")
                .setDescription("Test description")
                .setLocation(location)
                .setFrom(musicianType1)
                .setSearching(musicianType2)
                .setStyles(new HashSet<>(Arrays.asList(style)))
                .build();
        Ad newerAd = new AdBuilder().setId(2).setCreatedAt(Instant.now())
                .setPostedBy(testUser)
                .setTitle("Initial Test Ad")
                .setDescription("Test description")
                .setLocation(location)
                .setFrom(musicianType1)
                .setSearching(musicianType2)
                .setStyles(new HashSet<>(Arrays.asList(style)))
                .build();
        user.addLikedAd(newerAd);
        user.addLikedAd(olderAd);

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(musicianTypeService.findByName(musicianType1.getName())).thenReturn(musicianType1);
        Mockito.when(musicianTypeService.findByName(musicianType2.getName())).thenReturn(musicianType2);
        Mockito.when(styleService.findByName(style.getName())).thenReturn(style);
        Mockito.when(locationService.findByName(location.getName())).thenReturn(location);

        mockMvc.perform(get("/api/ads/favorites/{id}", userId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(newerAd.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", is(olderAd.getId())));
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void getFavoriteAds_ReturnsNotFound_WhenNoLikedAdsFound() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);

        Mockito.when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/ads/favorites/{id}", userId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test the toggle like/unlike ad endpoint.
     * @throws Exception
     */

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void toggleFavoriteAd_ReturnsOk_WhenAdExistsAndUserIsAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        int adId = 1;
        User user = new User(userId, "validUser", "validUser@example.com", "password", "profilePicture", true, null, null, null);
        Ad ad = new Ad();
        ad.setId(adId);

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.of(ad));

        mockMvc.perform(post("/api/ads/favorites/{userId}/{adId}", userId, adId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "validUser", roles = {"USER"})
    void toggleFavoriteAd_ReturnsBadRequest_WhenAdIsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        int adId = 999;

        Mockito.when(userService.getUserById(userId)).thenReturn(new User());
        Mockito.when(adService.getAdById(adId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/ads/favorites/{userId}/{adId}", userId, adId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
