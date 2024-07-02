package com.mariekd.letsplay.app.services.implementation;

import org.springframework.stereotype.Service;

import com.mariekd.letsplay.app.entities.MusicianType;
import com.mariekd.letsplay.app.repositories.MusicianTypeRepository;
import com.mariekd.letsplay.app.services.MusicianTypeService;

@Service
public class MusicianTypeServiceImpl implements MusicianTypeService {

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
