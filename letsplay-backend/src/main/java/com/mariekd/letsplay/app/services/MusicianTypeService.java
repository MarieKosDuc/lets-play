package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.MusicianType;
import org.springframework.stereotype.Service;

@Service
public interface MusicianTypeService {

    MusicianType findByName(String name);

    Boolean existsByName(String name);

}
