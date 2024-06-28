package com.mariekd.letsplay.authentication.payload.response;

import java.util.List;
import java.util.UUID;

public class LoginOkResponse {
    private String refreshToken;
    private String type = "Bearer";
    private UUID id;
    private String name;
    private String profilePicture;
    private String email;
    private List<String> roles;

    public LoginOkResponse(String refreshToken, UUID id, String name, String profilePicture, String email, List<String> roles) {
        this.refreshToken = refreshToken;
        this.id = id;
        this.name = name;
        this.profilePicture = profilePicture;
        this.email = email;
        this.roles = roles;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
