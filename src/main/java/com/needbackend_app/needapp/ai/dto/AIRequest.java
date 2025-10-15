package com.needbackend_app.needapp.ai.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record AIRequest(
    @NotBlank(message = "Prompt is required")
    String prompt,
    String systemContext,
    String userContext,
    String model,
    List<String> constraints
) {}