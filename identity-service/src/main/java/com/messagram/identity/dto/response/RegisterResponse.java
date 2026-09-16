package com.messagram.identity.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserRegistrationResponse(
        UUID userId,
        String email,
        Instant createdAt
) {
}
