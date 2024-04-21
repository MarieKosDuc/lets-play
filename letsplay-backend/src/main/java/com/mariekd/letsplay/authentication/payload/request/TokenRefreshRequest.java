package com.mariekd.letsplay.authentication.payload.request;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequest {

    @NotBlank
    private String token;

    public TokenRefreshRequest() {
    }

    public TokenRefreshRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
