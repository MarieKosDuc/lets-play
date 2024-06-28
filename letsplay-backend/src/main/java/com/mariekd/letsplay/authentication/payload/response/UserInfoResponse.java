package com.mariekd.letsplay.authentication.payload.response;

import java.util.List;
import java.util.UUID;

public record UserInfoResponse(UUID id, String username, String profilePicture) {
}
