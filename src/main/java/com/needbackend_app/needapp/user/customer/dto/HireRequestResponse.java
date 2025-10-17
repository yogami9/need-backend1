package com.needbackend_app.needapp.user.customer.dto;

import com.needbackend_app.needapp.user.enums.HireStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record HireRequestResponse(
        UUID requestId,
        UUID expertId,
        String expertName,
        String serviceDescription,
        HireStatus status,
        LocalDateTime createdAt
) {}

