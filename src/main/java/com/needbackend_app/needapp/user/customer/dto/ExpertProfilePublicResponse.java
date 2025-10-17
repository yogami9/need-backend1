package com.needbackend_app.needapp.user.customer.dto;

import java.util.List;
import java.util.UUID;

public record ExpertProfilePublicResponse(
        UUID id,
        String fullName,
        String category,
        List<String> subCategories,
        String avatarUrl,
        String portfolioUrl,
        String location,
        Integer yearsOfExperience,
        Double rating
) {}

