package com.mariekd.letsplay.app.dto.mappers;

import com.mariekd.letsplay.app.dto.UserDTO;
import com.mariekd.letsplay.authentication.entities.User;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }
}