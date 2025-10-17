package com.needbackend_app.needapp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryRecommendationRequest(
    @NotBlank(message = "Service description is required")
    @Schema(description = "Description of the service needed", example = "I need someone to fix my kitchen sink plumbing")
    String serviceDescription
) {}