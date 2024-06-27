package com.mariekd.letsplay.app.controllers;

import com.mariekd.letsplay.app.dto.AdDTO;
import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.entities.Location;
import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.entities.Style;
import com.mariekd.letsplay.app.request.CreateAdRequest;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.app.services.implementation.LocationServiceImpl;
import com.mariekd.letsplay.app.services.implementation.MusicianTypeServiceImpl;
import com.mariekd.letsplay.app.services.implementation.StyleServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdControllerTest {

    @InjectMocks
    private AdController adController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AdServiceImpl adService;

    @Mock
    private MusicianTypeServiceImpl musicianTypeService;

    @Mock
    private StyleServiceImpl styleService;

    @Mock
    private LocationServiceImpl locationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAdByIdSuccessfully() {
        Ad ad = new Ad();
        ad.setId(1);
        ad.setTitle("Test Ad");

        when(adService.getAdById(anyInt())).thenReturn(Optional.of(ad));

        ResponseEntity<AdDTO> response = adController.getAdById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ad.getId(), response.getBody().getId());
    }

    @Test
    public void getAdByIdFails() {
        when(adService.getAdById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<AdDTO> response = adController.getAdById(1);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void createAdSuccessfully() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");

        Style style = new Style(1, "Test style");
        MusicianType from = new MusicianType(1, "Test from", null);
        MusicianType searching = new MusicianType(2, "Test searching", null);
        Location location = new Location(1, "Test location", null);


        String[] styles = new String[1];
        styles[0] = "Test style";

        CreateAdRequest createAdRequest = new CreateAdRequest("Test ad", "Test from", "Test searching",
                "image.com", styles, "Test location", "Test description");

        when(userService.getUserFromRequest(any())).thenReturn(user);
        when(adService.getAdByTitle(anyString())).thenReturn(Optional.empty());

        when(userService.getUserFromRequest(any())).thenReturn(user);
        when(adService.getAdByTitle(anyString())).thenReturn(Optional.empty());

        when(musicianTypeService.findByName(from.getName())).thenReturn(from);
        when(musicianTypeService.findByName(searching.getName())).thenReturn(searching);
        when(locationService.findByName(location.getName())).thenReturn(location);
        when(styleService.findByName(styles[0])).thenReturn(new Style(1, styles[0]));        when (styleService.findByName("Test style")).thenReturn(style);

        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Object> response = adController.createAd(createAdRequest, request);
        System.out.print(response.toString());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void createAdFailsDueToExistingTitle() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");

        Ad existingAd = new Ad();
        existingAd.setId(1);
        existingAd.setTitle("Test Ad");

        String[] styles = new String[1];

        CreateAdRequest createAdRequest = new CreateAdRequest("Test ad", "Test from", "Test searching",
                "image.com", styles, "Test location", "Test description");

        when(userService.getUserFromRequest(any())).thenReturn(user);
        when(adService.getAdByTitle(anyString())).thenReturn(Optional.of(existingAd));

        ResponseEntity<Object> response = adController.createAd(createAdRequest, null);

        assertEquals(400, response.getStatusCodeValue());
    }

    //TODO : continuer les tests
}