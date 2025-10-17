package com.needbackend_app.needapp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChatSupportRequest(
    @NotBlank(message = "Message is required")
    @Schema(description = "User's message or question", example = "How do I find a reliable plumber?")
    String message,
    
    @Schema(description = "Additional context about the user's situation", example = "I'm a new customer looking for my first service")
    String context
) {}