package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.entities.Location;
import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.entities.Style;
import com.mariekd.letsplay.app.repositories.AdRepository;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdServiceImplTest {

    @InjectMocks
    private AdServiceImpl adService;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllAdsReturnsEmptyList() {
        when(adRepository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(0, adService.getAllAds().size());
        verify(adRepository, times(1)).findAll();
    }

    @Test
    public void getAllAdsReturnsListOfAds() {
        Instant createdAt = Instant.now();
        User user1 = new User();
        User user2 = new User();
        MusicianType from = new MusicianType();
        MusicianType searching = new MusicianType();
        Style style1 = new Style(1, "Style 1");
        Style style2 = new Style(2, "Style 2");
        Style style3 = new Style(3, "Style 3");
        Set<Style> styles1 = new HashSet<>(style1.getId(), style2.getId());
        Set<Style> styles2 = new HashSet<>(style3.getId());
        Location location1 = new Location(1, "Location 1", null);

        List<Ad> ads = new ArrayList<>();
        Ad ad1 = new Ad(1, createdAt, user1, "Annonce 1", from, searching, "image", styles1, location1, "Test add1", null);
        Ad ad2 = new Ad(2, createdAt, user2, "Annonce 2", from, searching, "image", styles2, location1, "Test add2", null);
        ads.add(ad1);
        ads.add(ad2);

        when(adRepository.findAll()).thenReturn(ads);

        List<Ad> returnedAds = adService.getAllAds();

        assertEquals(ads, returnedAds);
        verify(adRepository, times(1)).findAll();
        assertEquals(2, returnedAds.size());
        assertEquals(ad1, returnedAds.get(0));
        assertEquals(ad2, returnedAds.get(1));
    }

    @Test
    public void getAdByIdReturnsAd() {
        Ad ad = new Ad();
        when(adRepository.findById(anyInt())).thenReturn(Optional.of(ad));

        assertEquals(ad, adService.getAdById(1).get());
        verify(adRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getAdByIdReturnsEmpty() {
        when(adRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), adService.getAdById(1));
        verify(adRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getAdByTitleReturnsAd() {
        Ad ad = new Ad();
        when(adRepository.findByTitle(anyString())).thenReturn(Optional.of(ad));

        assertEquals(ad, adService.getAdByTitle("Annonce 1").get());
        verify(adRepository, times(1)).findByTitle(anyString());
    }

    @Test
    public void createAdReturnsAd() {
        Ad ad = new Ad();
        when(adRepository.save(any(Ad.class))).thenReturn(ad);

        assertEquals(ad, adService.createAd(ad));
        verify(adRepository, times(1)).save(any(Ad.class));
    }
}