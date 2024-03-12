package com.mariekd.letsplay.authentication.payload.request;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(@NotBlank String username, @NotBlank String email, @NotBlank String password) {
}
