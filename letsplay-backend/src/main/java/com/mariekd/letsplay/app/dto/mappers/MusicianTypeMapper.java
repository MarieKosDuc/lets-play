package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.app.dto.MusicianTypeDTO;
import com.mariekd.letsplay.app.entities.MusicianType;

public class MusicianTypeMapper {

    public static MusicianTypeDTO toMusicianDTO (MusicianType musician) {
        MusicianTypeDTO musicianDTO = new MusicianTypeDTO();
        musicianDTO.setId(musician.getId());
        musicianDTO.setName(musician.getName());
        return musicianDTO;
    }
}