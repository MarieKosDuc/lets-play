package com.mariekd.letsplay.authentication.dto;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String profilePicture;
    private String password;

    public UserDTO(UUID id, String name, String email, String password, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.password = password;
    }

    public UserDTO() {
    }

    public UserDTO(UUID id, String name, String email, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return password; }
}
