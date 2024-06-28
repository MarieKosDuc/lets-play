package com.mariekd.letsplay.authentication.controller;

import com.mariekd.letsplay.app.services.AdService;
import com.mariekd.letsplay.authentication.dto.UserDTO;
import com.mariekd.letsplay.authentication.entities.User;
import com.mariekd.letsplay.authentication.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(maxAge = 3600)
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final AdService adService;

    public AdminController(UserService userService, AdService adService) {
        this.userService = userService;
        this.adService = adService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        LOGGER.info("Getting all users for admin");
        LOGGER.info("User is admin: " + userService.getUserByEmail("eira_de_merilme@hotmail.com"));

        try {
            List<User> usersList = userService.getAllUsers();
            List<UserDTO> usersDTOList = new ArrayList<>();
            for (User user : usersList) {
                usersDTOList.add(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getProfilePicture()));
            }
            return usersDTOList;
        } catch (final Exception e) {
            LOGGER.error("Error getting all users for admin: " + e.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting all users for admin");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUserByAdmin(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (final Exception e) {
            LOGGER.error("Error deleting user by admin: " + e.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user by admin");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Object> deleteAdByAdmin(@PathVariable("id") int id) throws HttpServerErrorException {
        try {
            adService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting ad");
        }
    }
}
