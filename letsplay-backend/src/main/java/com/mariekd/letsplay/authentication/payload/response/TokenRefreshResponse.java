package com.mariekd.letsplay.authentication.payload.response;

public class TokenRefreshResponse {

    private String refreshToken;
    private String tokenType = "Bearer";

    private String tokenExpiration;

    public TokenRefreshResponse(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
