package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.dto.CustomerProfileResponse;
import com.needbackend_app.needapp.user.customer.dto.UpdateCustomerProfileRequest;
import com.needbackend_app.needapp.user.customer.service.CustomerProfileService;
import com.needbackend_app.needapp.user.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/profile")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for managing customers users")
@SecurityRequirement(name = "bearerAuth")
public class CustomerProfileController {

    private final CustomerProfileService profileService;

    @Operation(summary = "Get current customer profile")
    @GetMapping
    public ResponseEntity<CustomerProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @Operation(summary = "Update customer profile")
    @PatchMapping
    public ResponseEntity<ApiResponse> updateProfile(@Valid @RequestBody UpdateCustomerProfileRequest request) {
        ApiResponse response = profileService.updateProfile(request);
        return ResponseEntity.ok(response);
    }
}
