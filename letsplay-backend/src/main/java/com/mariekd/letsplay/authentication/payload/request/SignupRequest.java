package com.mariekd.letsplay.authentication.payload.request;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(@NotBlank String name, @NotBlank String email, String profilePicture, @NotBlank String password) {
}
