package com.mariekd.letsplay.app.services;

import com.mariekd.letsplay.app.entities.Location;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {

    Location findByName(String name);

    Boolean existsByName(String name);

}
