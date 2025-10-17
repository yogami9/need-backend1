package com.needbackend_app.needapp.user.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerSignupRequest(

        @Schema(description = "First name of the customer", example = "Ada")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last name of the customer", example = "Smith")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Phone number", example = "08012345678")
        @NotBlank(message = "Phone number is required")
        String phone,

        @Schema(description = "Password", example = "securePassword123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Schema(description = "Confirm password", example = "securePassword123")
        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {}
