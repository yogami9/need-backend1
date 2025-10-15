package com.needbackend_app.needapp.user.customer.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID senderId,
        String senderRole,
        String content,
        Instant timestamp
) {}
