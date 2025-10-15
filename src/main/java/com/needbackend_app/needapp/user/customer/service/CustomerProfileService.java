package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CustomerProfileResponse;
import com.needbackend_app.needapp.user.customer.dto.UpdateCustomerProfileRequest;
import com.needbackend_app.needapp.user.util.ApiResponse;

public interface CustomerProfileService {
    CustomerProfileResponse getCurrentProfile();

    ApiResponse updateProfile(UpdateCustomerProfileRequest request);
}
