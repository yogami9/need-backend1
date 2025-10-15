package com.needbackend_app.needapp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ContentModerationRequest(
    @NotBlank(message = "Content is required")
    @Schema(description = "Content to be moderated", example = "I provide excellent plumbing services in Lagos")
    String content
) {}