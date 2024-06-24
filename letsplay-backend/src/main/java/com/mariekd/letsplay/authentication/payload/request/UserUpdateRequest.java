package com.mariekd.letsplay.authentication.payload.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(@NotBlank String name, @NotBlank String profilePicture) {
}
