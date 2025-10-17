package com.needbackend_app.needapp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ServiceDescriptionRequest(
    @NotBlank(message = "Category is required")
    @Schema(description = "Main service category", example = "Plumbing")
    String category,
    
    @NotEmpty(message = "At least one subcategory is required")
    @Schema(description = "List of subcategories", example = "[\"Pipe Installation\", \"Drain Repair\"]")
    List<String> subCategories,
    
    @Schema(description = "Additional details about the service", example = "5 years experience, licensed professional")
    String additionalDetails
) {}