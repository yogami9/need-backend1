package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.HireRequestCreateRequest;
import com.needbackend_app.needapp.user.customer.dto.HireRequestResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface HireRequestService {
    HireRequestResponse createHireRequest(UUID customerId, HireRequestCreateRequest request);
    void cancelHireRequest(UUID customerId, UUID requestId) throws AccessDeniedException;
    List<HireRequestResponse> getMyRequests(UUID customerId);
}

