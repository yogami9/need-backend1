package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.auth.jwt.JwtTokenProvider;
import com.needbackend_app.needapp.user.customer.service.CustomerSignUpService;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.user.customer.dto.CustomerSignupRequest;
import com.needbackend_app.needapp.user.customer.model.CustomerProfile;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import com.needbackend_app.needapp.user.util.ApiResponse;
import com.needbackend_app.needapp.user.util.PasswordValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerSignUpServiceImpl implements CustomerSignUpService {

    private final NeedUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public ApiResponse registerCustomer(CustomerSignupRequest request) {
        PasswordValidation.validatePasswords(request.password(), request.confirmPassword());

        if (userRepository.existsByPhone(request.phone()))
            throw new IllegalArgumentException("Phone number already exists");

        NeedUser user = new NeedUser();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);

        CustomerProfile profile = new CustomerProfile();
        profile.setNeedUser(user);
        user.setCustomerProfile(profile);

        userRepository.save(user);


        var authorities = List.of(new SimpleGrantedAuthority(user.getRoleAsAuthority()));
        var auth = new UsernamePasswordAuthenticationToken(user.getId().toString(), null, authorities);
        String token = tokenProvider.accessToken(user, auth);

        return new ApiResponse(true, Map.of(
                "access_token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "phone", user.getPhone(),
                        "role", user.getRoleAsAuthority(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                )
        ), "Customer registered successfully.");
    }

}
