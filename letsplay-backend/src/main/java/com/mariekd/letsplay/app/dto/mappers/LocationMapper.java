package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.app.dto.LocationDTO;
import com.mariekd.letsplay.app.entities.Location;

public class LocationMapper {
    public static LocationDTO toLocationDTO (Location location) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(location.getId());
        locationDTO.setName(location.getName());
        return locationDTO;
    }
}