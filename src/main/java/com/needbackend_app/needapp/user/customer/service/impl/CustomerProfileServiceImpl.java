package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.auth.util.AuthenticatedUserProvider;
import com.needbackend_app.needapp.user.customer.dto.CustomerProfileResponse;
import com.needbackend_app.needapp.user.customer.dto.UpdateCustomerProfileRequest;
import com.needbackend_app.needapp.user.customer.model.CustomerProfile;
import com.needbackend_app.needapp.user.customer.repository.CustomerProfileRepository;
import com.needbackend_app.needapp.user.customer.service.CustomerProfileService;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.util.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public CustomerProfileResponse getCurrentProfile() {
        NeedUser user = authenticatedUserProvider.getCurrentUser();
        CustomerProfile profile = customerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found."));

        return new CustomerProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                profile.getGender(),
                profile.getAddress(),
                profile.getAvatarUrl()
        );
    }

    @Override
    public ApiResponse updateProfile(UpdateCustomerProfileRequest request) {
        NeedUser user = authenticatedUserProvider.getCurrentUser();
        CustomerProfile profile = customerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found."));

        profile.setAddress(request.address());
        profile.setGender(request.gender());
        profile.setAvatarUrl(request.avatarUrl());
        customerProfileRepository.save(profile);

        return new ApiResponse(true, null, "Profile updated successfully");
    }
}
