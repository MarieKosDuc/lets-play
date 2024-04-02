package com.mariekd.letsplay.app.repositories;

import com.mariekd.letsplay.app.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Location findByName(String name);

    Boolean existsByName(String name);


}
