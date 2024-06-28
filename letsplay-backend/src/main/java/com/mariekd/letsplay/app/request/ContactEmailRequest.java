package com.mariekd.letsplay.app.request;

import jakarta.validation.constraints.NotBlank;

public record ContactEmailRequest(@NotBlank String adId, @NotBlank String fromUser, String messageContent) {
}
