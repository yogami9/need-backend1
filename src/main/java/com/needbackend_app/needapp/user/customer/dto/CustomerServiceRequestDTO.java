package com.needbackend_app.needapp.user.customer.dto;

import java.util.UUID;


public record CustomerServiceRequestDTO (
     UUID categoryId,
     UUID subCategoryId,
     String description
) {}
