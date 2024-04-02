package com.mariekd.letsplay.app.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAdRequest(@NotBlank String title, @NotBlank String musicianType, @NotBlank String image,
                              @NotBlank String[] styles, @NotBlank String location, @NotBlank String description) {
}

