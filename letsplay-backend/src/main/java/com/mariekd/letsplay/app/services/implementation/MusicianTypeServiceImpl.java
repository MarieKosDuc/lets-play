package com.mariekd.letsplay.app.services.implementation;

import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.repositories.MusicianTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class MusicianTypeServiceImpl {

    final MusicianTypeRepository musicianTypeRepository;

    public MusicianTypeServiceImpl(MusicianTypeRepository MusicianTypeRepository) {
        this.musicianTypeRepository = MusicianTypeRepository;
    }
    public MusicianType findByName(String name) {
        return musicianTypeRepository.findByName(name);
    }

    public Boolean existsByName(String name) {
        return musicianTypeRepository.existsByName(name);
    }
}
