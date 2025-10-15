package com.needbackend_app.needapp.administrator.admin.service;

import com.needbackend_app.needapp.administrator.admin.dto.AdminCreateRequest;
import com.needbackend_app.needapp.administrator.admin.dto.AdminResponseDTO;
import com.needbackend_app.needapp.administrator.admin.model.AdminProfile;
import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import com.needbackend_app.needapp.user.util.ApiResponse;
import com.needbackend_app.needapp.user.util.PasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCreateService {

    private final NeedUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse createAdmin(AdminCreateRequest request) {
        PasswordValidation.validatePasswords(request.password(), request.confirmPassword());

        if (userRepository.existsByEmail(request.email()))
            throw new IllegalArgumentException("Email already exists");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String superAdminIdentifier = auth.getName();

        NeedUser superAdmin = userRepository.findById(UUID.fromString(superAdminIdentifier))
                .orElseThrow(() -> new UsernameNotFoundException("SuperAdmin not found."));

        log.info("SuperAdmin {} is creating a new Admin [{} {}] for State [{}]",
                superAdmin.getEmail(),
                request.firstName(),
                request.lastName(),
                request.assignedState());

        NeedUser user = new NeedUser();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ADMIN);

        AdminProfile profile = new AdminProfile();
        profile.setAssignedState(request.assignedState());
        profile.setNotes(request.note());
        profile.setNeedUser(user);

        user.setAdminProfile(profile);
        userRepository.save(user);
        return new ApiResponse(true, AdminResponseDTO.fromEntity(user), "Admin created successfully.");
    }
}