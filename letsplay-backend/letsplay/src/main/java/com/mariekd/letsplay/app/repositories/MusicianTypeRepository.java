package com.mariekd.letsplay.app.repositories;

import com.mariekd.letsplay.app.entities.MusicianType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicianTypeRepository extends JpaRepository<MusicianType, String> {
}
