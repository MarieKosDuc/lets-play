package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.app.dto.StyleDTO;
import com.mariekd.letsplay.app.entities.Style;

public class StyleMapper {
    public static StyleDTO toStyleDTO (Style style) {
        StyleDTO styleDTO = new StyleDTO();
        styleDTO.setId(style.getId());
        styleDTO.setName(style.getName());
        return styleDTO;
    }
}