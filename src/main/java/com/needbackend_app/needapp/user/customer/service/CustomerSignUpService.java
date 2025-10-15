package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CustomerSignupRequest;
import com.needbackend_app.needapp.user.util.ApiResponse;

public interface CustomerSignUpService {
    ApiResponse registerCustomer(CustomerSignupRequest request);
}
