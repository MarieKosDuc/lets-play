package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Style;
import org.springframework.stereotype.Service;

@Service
public interface StyleService {

    Style findByName(String name);

    Boolean existsByName(String name);

}
