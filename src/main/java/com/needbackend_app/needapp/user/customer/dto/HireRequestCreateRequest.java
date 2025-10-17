package com.needbackend_app.needapp.user.customer.dto;

import java.util.UUID;

public record HireRequestCreateRequest(
        UUID expertId,
        String serviceDescription
) {}

