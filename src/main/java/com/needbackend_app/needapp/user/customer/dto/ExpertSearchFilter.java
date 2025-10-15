package com.needbackend_app.needapp.user.customer.dto;

import java.util.List;

public record ExpertSearchFilter(
        String category,
        List<String> subCategories,
        String location,
        Integer minExperience,
        Integer maxExperience
) {}

