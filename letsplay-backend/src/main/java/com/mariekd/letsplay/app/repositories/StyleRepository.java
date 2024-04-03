package com.mariekd.letsplay.app.repositories;

import com.mariekd.letsplay.app.entities.Style;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleRepository extends JpaRepository<Style, Integer> {

    Style findByName(String name);

    Boolean existsByName(String name);


}
