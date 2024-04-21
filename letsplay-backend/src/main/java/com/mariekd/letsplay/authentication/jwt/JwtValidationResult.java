package com.mariekd.letsplay.authentication.jwt;

public class JwtValidationResult {

    private boolean valid;
    private boolean expired;
    private String errorMessage;

    public JwtValidationResult(boolean valid, boolean expired, String errorMessage) {
        this.valid = valid;
        this.expired = expired;
        this.errorMessage = errorMessage;
    }

    public JwtValidationResult() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
