package com.needbackend_app.needapp.administrator.admin.dto;

import com.needbackend_app.needapp.administrator.admin.model.AdminProfile;
import com.needbackend_app.needapp.user.model.NeedUser;

import java.util.UUID;

public record AdminResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String assignedState,
        String note,
        String role
) {
    public static AdminResponseDTO fromEntity(NeedUser user) {
        AdminProfile profile = user.getAdminProfile();
        return new AdminResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                profile != null ? profile.getAssignedState() : null,
                profile != null ? profile.getNotes() : null,
                user.getRole().name()
        );
    }
}

