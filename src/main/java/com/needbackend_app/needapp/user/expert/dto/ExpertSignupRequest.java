package com.needbackend_app.needapp.user.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record ExpertSignupRequest(
        @Schema(description = "First name of the customer", example = "Ada")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(description = "Last name of the customer", example = "Smith")
        @NotBlank(message = "Full name is required")
        String lastName,

        @Schema(description = "Phone number", example = "08123456789")
        @NotBlank
        String phone,

        @Schema(description = "Password", example = "strongPass!")
        @NotBlank
        @Size(min = 6)
        String password,

        @Schema(description = "Confirm password", example = "strongPass!")
        @NotBlank
        String confirmPassword,

        @Schema(description = "Main service category", example = "Plumbing")
        @NotBlank
        String category,

        @Schema(description = "Sub-categories of expertise", example = "[\"Pipe Installation\", \"Drain Repair\"]")
        @NotEmpty(message = "At least one subcategory is required")
        List<@NotBlank String> subCategories,

        @Schema(description = "Years of experience", example = "5")
        @NotNull
        Integer yearsOfExperience,

        @Schema(description = "Portfolio link", example = "https://myportfolio.com/john")
        String portfolioUrl,

        @Schema(description = "National ID number", example = "A12345678")
        @NotBlank
        String nationalId
) {}
