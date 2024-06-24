package com.mariekd.letsplay.authentication.dto.mappers;

import com.mariekd.letsplay.app.dto.UserLightDTO;
import com.mariekd.letsplay.authentication.dto.UserDTO;
import com.mariekd.letsplay.authentication.entities.User;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setProfilePicture(user.getProfilePicture());
        return userDTO;
    }

    public static User fromUserDTO(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setProfilePicture(userDTO.getProfilePicture());
        return user;
    }
}