package com.mariekd.letsplay.app.services.implementation;

import com.mariekd.letsplay.app.entities.Location;
import com.mariekd.letsplay.app.repositories.LocationRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl {

    final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }
    public Location findByName(String name) {
        return locationRepository.findByName(name);
    }

    public Boolean existsByName(String name) {
        return locationRepository.existsByName(name);
    }
}
