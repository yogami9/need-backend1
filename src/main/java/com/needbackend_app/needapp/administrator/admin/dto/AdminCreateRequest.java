package com.needbackend_app.needapp.administrator.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCreateRequest(
        @Schema(description = "First name of the admin", example = "Ada")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last name of the admin", example = "Smith")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(description = "Email address", example = "grace@need.com")
        @Email
        @NotBlank
        String email,

        @Schema(description = "Phone number", example = "09012345678")
        @NotBlank
        String phone,

        @Schema(description = "Password", example = "adminSecurePass")
        @NotBlank
        @Size(min = 6)
        String password,

        @Schema(description = "Confirm password", example = "adminSecurePass")
        @NotBlank
        String confirmPassword,

        @Schema(description = "State assigned to the admin", example = "Abuja")
        @NotBlank
        String assignedState,

        @Schema(description = "Notes or additional info about the admin", example = "Handles expert approvals for the Abuja region")
        String note
) {}
