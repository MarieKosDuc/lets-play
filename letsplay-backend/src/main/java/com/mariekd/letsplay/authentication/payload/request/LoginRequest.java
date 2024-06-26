package com.mariekd.letsplay.authentication.payload.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (@NotBlank String name, @NotBlank String password) {
}
