package com.mariekd.letsplay.app.controllers;


import com.mariekd.letsplay.app.entities.*;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.authentication.entities.Role;
import com.mariekd.letsplay.authentication.entities.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.*;



@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;

    @InjectMocks
    private AdController adController;

    private Ad ad1, ad2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Role userRole = new Role(1L, "USER", null);

        User testUser = new User(UUID.randomUUID(), "testUser", "test@mail.com", "testPassword",
                "profilePicture.jpg", true, userRole,new HashSet<>(Arrays.asList(ad1, ad2)), new HashSet<>());

        Instant now = Instant.now();

        Style style1 = new Style(1, "TestStyle1", new HashSet<>());

        Style style2 = new Style(2, "TestStyle2", new HashSet<>());

        Location location = new Location(1, "TestLocation", new HashSet<>());

        MusicianType musicianType1 = new MusicianType(1, "TestMusicianType1", new HashSet<>());

        MusicianType musicianType2 = new MusicianType(2, "TestMusicianType2", new HashSet<>());

        ad1 = new AdBuilder()
                .setId(1)
                .setTitle("Ad 1")
                .setDescription("Description 1")
                .setPostedBy(testUser)
                .setCreatedAt(now)
                .setImage("image.jpg")
                .setStyles(new HashSet<>(Arrays.asList(style1)))
                .setLocation(location)
                .setFrom(musicianType1)
                .setSearching(musicianType2)
                .setDescription("This is a test ad")
                .build();

        ad2 = new AdBuilder()
                .setId(2)
                .setTitle("Ad 2")
                .setDescription("Description 2")
                .setPostedBy(testUser)
                .setCreatedAt(now.plusMillis(1000))
                .setImage("image2.jpg")
                .setStyles(new HashSet<>(Arrays.asList(style1, style2)))
                .setLocation(location)
                .setFrom(musicianType1)
                .setSearching(musicianType2)
                .setDescription("This is a second test ad")
                .build();

        when(adService.getAllAdsSortedByReversedDate()).thenReturn(Arrays.asList(ad1, ad2));
        when(adService.getAdById(1)).thenReturn(Optional.of(ad1));
    }

    @Test
    public void getAllAds_ReturnsAllAds() throws Exception {
        mockMvc.perform(get("/api/ads/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Ad 1"))
                .andExpect(jsonPath("$[1].title").value("Ad 2"));
    }

    @Test
    public void getAdById_ReturnsAd() throws Exception {
        mockMvc.perform(get("/api/ads/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ad 1"));
    }

    @Test
    public void searchAds_ReturnsOkAndBodyWithAdsInReverseOrder_WhenAdsFound() throws Exception {
        String fromMusicianType = "TestMusicianType1";
        String searchingMusicianType = "TestMusicianType2";
        List<String> styles = Arrays.asList("TestStyle1");
        String location = "TestLocation";

        when(adService.getSearchedAds(fromMusicianType, searchingMusicianType, styles, location)).thenReturn(Arrays.asList(ad1, ad2));

        mockMvc.perform(get("/api/ads/search")
                        .param("from", fromMusicianType)
                        .param("searching", searchingMusicianType)
                        .param("styles", String.join(",", styles))
                        .param("location", location)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Ad 2"))
                .andExpect(jsonPath("$[1].title").value("Ad 1"));
    }

    @Test
    public void searchAds_ReturnsEmptyBody_WhenNoAdsFound() throws Exception {
        String fromMusicianType = "TestMusicianType1";
        String searchingMusicianType = "TestMusicianType2";
        List<String> styles = Arrays.asList("TestStyle1");
        String location = "TestLocation";

        mockMvc.perform(get("/api/ads/search")
                        .param("from", fromMusicianType)
                        .param("searching", searchingMusicianType)
                        .param("styles", String.join(",", styles))
                        .param("location", location)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}