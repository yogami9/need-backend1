package com.needbackend_app.needapp.user.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record UpdateCustomerProfileRequest(
        @Schema(description = "Customer address", example = "23 Victory St, Ikeja, Lagos")
        String address,

        @Schema(description = "Avatar image URL", example = "https://cdn.needapp.com/images/avatar123.jpg")
        String avatarUrl,

        @Schema(description = "Gender", example = "FEMALE")
        @Pattern(regexp = "MALE|FEMALE", message = "Gender must be MALE, or FEMALE")
        String gender
) {}

