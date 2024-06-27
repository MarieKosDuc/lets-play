package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.repositories.MusicianTypeRepository;
import com.mariekd.letsplay.app.services.implementation.MusicianTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MusicianTypeServiceImplTest {

    @InjectMocks
    private MusicianTypeServiceImpl musicianTypeService;

    @Mock
    private MusicianTypeRepository musicianTypeRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findByNameReturnsMusicianType() {
        MusicianType musicianType = new MusicianType();
        when(musicianTypeRepository.findByName(anyString())).thenReturn(musicianType);

        assertEquals(musicianType, musicianTypeService.findByName("MusicianTypeName"));
        verify(musicianTypeRepository, times(1)).findByName(anyString());
    }

    @Test
    public void findByNameReturnsNull() {
        when(musicianTypeRepository.findByName(anyString())).thenReturn(null);

        assertNull(musicianTypeService.findByName("MusicianTypeName"));
        verify(musicianTypeRepository, times(1)).findByName(anyString());
    }

    @Test
    public void existsByNameReturnsTrue() {
        when(musicianTypeRepository.existsByName(anyString())).thenReturn(true);

        assertTrue(musicianTypeService.existsByName("MusicianTypeName"));
        verify(musicianTypeRepository, times(1)).existsByName(anyString());
    }

    @Test
    public void existsByNameReturnsFalse() {
        when(musicianTypeRepository.existsByName(anyString())).thenReturn(false);

        assertFalse(musicianTypeService.existsByName("MusicianTypeName"));
        verify(musicianTypeRepository, times(1)).existsByName(anyString());
    }
}