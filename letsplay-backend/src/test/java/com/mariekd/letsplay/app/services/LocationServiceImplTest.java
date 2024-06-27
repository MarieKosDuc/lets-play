package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Location;
import com.mariekd.letsplay.app.repositories.LocationRepository;
import com.mariekd.letsplay.app.services.implementation.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LocationServiceImplTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findByNameReturnsLocation() {
        Location location = new Location();
        when(locationRepository.findByName(anyString())).thenReturn(location);

        assertEquals(location, locationService.findByName("LocationName"));
        verify(locationRepository, times(1)).findByName(anyString());
    }

    @Test
    public void findByNameReturnsNull() {
        when(locationRepository.findByName(anyString())).thenReturn(null);

        assertNull(locationService.findByName("LocationName"));
        verify(locationRepository, times(1)).findByName(anyString());
    }

    @Test
    public void existsByNameReturnsTrue() {
        when(locationRepository.existsByName(anyString())).thenReturn(true);

        assertTrue(locationService.existsByName("LocationName"));
        verify(locationRepository, times(1)).existsByName(anyString());
    }

    @Test
    public void existsByNameReturnsFalse() {
        when(locationRepository.existsByName(anyString())).thenReturn(false);

        assertFalse(locationService.existsByName("LocationName"));
        verify(locationRepository, times(1)).existsByName(anyString());
    }
}