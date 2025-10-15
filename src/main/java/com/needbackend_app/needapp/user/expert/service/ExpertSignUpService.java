package com.needbackend_app.needapp.user.expert.service;

import com.needbackend_app.needapp.user.expert.dto.ExpertSignupRequest;
import com.needbackend_app.needapp.user.util.ApiResponse;

public interface ExpertSignUpService {
    ApiResponse registerExpert(ExpertSignupRequest request);
}
