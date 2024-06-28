package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.app.dto.AdDTO;
import com.mariekd.letsplay.app.entities.Ad;
import com.mariekd.letsplay.app.entities.Style;

public class AdMapper {
    public static AdDTO toAdDTO(Ad ad) {
        AdDTO adDTO = new AdDTO();

        String[] styleNames = ad.getStyles().stream().map(Style::getName).toArray(String[]::new);

        adDTO.setId(ad.getId());
        adDTO.setCreatedAt(ad.getCreatedAt());
        adDTO.setPostedByName(UserMapper.toUserDTO(ad.getPostedBy()).getName());
        adDTO.setPostedById(UserMapper.toUserDTO(ad.getPostedBy()).getId().toString());
        adDTO.setTitle(ad.getTitle());
        adDTO.setFrom(MusicianTypeMapper.toMusicianDTO(ad.getFrom()).getName());
        adDTO.setSearching(MusicianTypeMapper.toMusicianDTO(ad.getSearching()).getName());
        adDTO.setImage(ad.getImage());
        adDTO.setStyles(styleNames);
        adDTO.setLocation(LocationMapper.toLocationDTO(ad.getLocation()).getName());
        adDTO.setDescription(ad.getDescription());

        return adDTO;
    }

}
