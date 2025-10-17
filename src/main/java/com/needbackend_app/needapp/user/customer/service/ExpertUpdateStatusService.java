package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.enums.RequestStatus;
import com.needbackend_app.needapp.user.util.ApiResponse;

import java.util.UUID;

public interface ExpertUpdateStatusService {
    public ApiResponse updateRequestStatus(UUID requestId, RequestStatus status);
}
