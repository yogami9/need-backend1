package com.needbackend_app.needapp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "Email must be valid")
        @Schema(example = "admin@need.com") String email,
        @Schema(example = "08012345678")String phone,
        @NotBlank(message = "Password is required") String password
) {
}


