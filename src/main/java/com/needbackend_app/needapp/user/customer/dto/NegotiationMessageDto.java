package com.needbackend_app.needapp.user.customer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NegotiationMessageDto(
        UUID hireRequestId,
        UUID sender,
        String message,
        LocalDateTime timestamp
) {}

