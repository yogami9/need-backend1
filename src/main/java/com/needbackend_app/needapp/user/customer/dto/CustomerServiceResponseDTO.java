package com.needbackend_app.needapp.user.customer.dto;

import com.needbackend_app.needapp.user.enums.RequestStatus;


import java.time.LocalDateTime;
import java.util.UUID;


public record CustomerServiceResponseDTO (
     UUID id,
     String description,
     String categoryName,
     String subCategoryName,
     RequestStatus status,
     LocalDateTime createdAt
     ) {}
