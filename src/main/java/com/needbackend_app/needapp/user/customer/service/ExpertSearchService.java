package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.ExpertProfilePublicResponse;
import com.needbackend_app.needapp.user.customer.dto.ExpertSearchFilter;

import java.util.List;
import java.util.UUID;

public interface ExpertSearchService {
    List<ExpertProfilePublicResponse> searchExperts(ExpertSearchFilter filter);
    ExpertProfilePublicResponse getExpertProfile(UUID expertId);
}

