package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Style;
import com.mariekd.letsplay.app.repositories.StyleRepository;
import com.mariekd.letsplay.app.services.implementation.StyleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StyleServiceImplTest {

    @InjectMocks
    private StyleServiceImpl styleService;

    @Mock
    private StyleRepository styleRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findByNameReturnsStyle() {
        Style style = new Style();
        when(styleRepository.findByName(anyString())).thenReturn(style);

        assertEquals(style, styleService.findByName("StyleName"));
        verify(styleRepository, times(1)).findByName(anyString());
    }

    @Test
    public void findByNameReturnsNull() {
        when(styleRepository.findByName(anyString())).thenReturn(null);

        assertNull(styleService.findByName("StyleName"));
        verify(styleRepository, times(1)).findByName(anyString());
    }

    @Test
    public void existsByNameReturnsTrue() {
        when(styleRepository.existsByName(anyString())).thenReturn(true);

        assertTrue(styleService.existsByName("StyleName"));
        verify(styleRepository, times(1)).existsByName(anyString());
    }

    @Test
    public void existsByNameReturnsFalse() {
        when(styleRepository.existsByName(anyString())).thenReturn(false);

        assertFalse(styleService.existsByName("StyleName"));
        verify(styleRepository, times(1)).existsByName(anyString());
    }
}