package com.mariekd.letsplay.app.services.implementation;

import com.mariekd.letsplay.app.entities.Style;
import com.mariekd.letsplay.app.repositories.StyleRepository;
import com.mariekd.letsplay.app.services.StyleService;
import org.springframework.stereotype.Service;

@Service
public class StyleServiceImpl implements StyleService {

    final StyleRepository styleRepository;

    public StyleServiceImpl(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    public Style findByName(String name) {
        return styleRepository.findByName(name);
    }

    public Boolean existsByName(String name) {
        return styleRepository.existsByName(name);
    }
}
