package com.needbackend_app.needapp.ai.dto;

import java.util.List;

public record CategoryRecommendationResponse(
    boolean success,
    String message,
    String recommendedCategory,
    List<String> recommendedSubCategories
) {}