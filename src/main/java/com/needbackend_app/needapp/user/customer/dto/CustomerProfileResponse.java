package com.needbackend_app.needapp.user.customer.dto;

public record CustomerProfileResponse(
        String firstName,
        String lastName,
        String email,
        String phone,
        String gender,
        String address,
        String avatarUrl
) {}
