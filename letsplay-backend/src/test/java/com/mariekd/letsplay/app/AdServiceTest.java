package com.mariekd.letsplay.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.repositories.AdRepository;
import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.app.services.implementation.AdServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllAdsSuccesfully() {
        Ad ad1 = new Ad();
        Ad ad2 = new Ad();
        when(adRepository.findAll()).thenReturn(Arrays.asList(ad1, ad2));

        List<Ad> result = adService.getAllAds();

        assertEquals(2, result.size());
    }

    @Test
    public void getAdByIdSuccesfully() {
        Ad ad = new Ad();
        ad.setId(1);
        when(adRepository.findById(1)).thenReturn(Optional.of(ad));

        Optional<Ad> result = adService.getAdById(1);

        assertEquals(ad.getId(), result.get().getId());
    }

    @Test
    public void getAllAdsSortedByReversedDate() {
        Instant now = Instant.now();
        Ad ad1 = new Ad();
        ad1.setCreatedAt(now);
        Ad ad2 = new Ad();
        ad2.setCreatedAt(now.plusSeconds(3600));
        when(adRepository.findAll()).thenReturn(Arrays.asList(ad1, ad2));

        List<Ad> result = adService.getAllAdsSortedByReversedDate();

        assertEquals(2, result.size());
        assertEquals(ad2.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals(ad1.getCreatedAt(), result.get(1).getCreatedAt());
    }

    @Test
    public void updateAd_ThrowsExceptionWhenAdDoesNotExist() {
        Ad ad = new Ad();
        ad.setId(1);
        when(adRepository.existsById(1)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> adService.updateAd(1, ad));
    }

}