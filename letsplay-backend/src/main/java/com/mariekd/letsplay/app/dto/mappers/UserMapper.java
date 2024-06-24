package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.authentication.dto.UserDTO;
import com.mariekd.letsplay.app.dto.UserLightDTO;
import com.mariekd.letsplay.authentication.entities.User;

public class UserMapper {

    public static UserLightDTO toUserLightDTO(User user) {
        UserLightDTO userLightDTO = new UserLightDTO();
        userLightDTO.setId(user.getId());
        userLightDTO.setName(user.getName());
        return userLightDTO;
    }
}