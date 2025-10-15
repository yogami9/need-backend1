package com.needbackend_app.needapp.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.needbackend_app.needapp.auth.dto.LoginRequest;
import com.needbackend_app.needapp.auth.jwt.JwtTokenProvider;
import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import com.needbackend_app.needapp.user.util.ApiErrorResponse;
import com.needbackend_app.needapp.user.util.ApiResponse;
import com.needbackend_app.needapp.user.util.ErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class AuthService extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final NeedUserRepository needUserRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

            String identifier = loginRequest.email() != null && !loginRequest.email().isBlank()
                    ? loginRequest.email()
                    : loginRequest.phone();

            if (identifier == null || identifier.isBlank()) {
                throw new AuthenticationServiceException("Identifier (email or phone) is required.");
            }

            NeedUser user = findUserByIdentifier(identifier);

            if (user.getRole() == Role.CUSTOMER || user.getRole() == Role.EXPERT) {
                if (loginRequest.phone() == null || loginRequest.phone().isBlank()) {
                    throw new AuthenticationServiceException("Phone number is required for this account.");
                }
                identifier = loginRequest.phone();
            } else if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN) {
                if (loginRequest.email() == null || loginRequest.email().isBlank()) {
                    throw new AuthenticationServiceException("Email is required for this account.");
                }
                identifier = loginRequest.email();
            }

            var authToken = new UsernamePasswordAuthenticationToken(identifier, loginRequest.password());
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            log.error("Invalid login request format: {}", e.getMessage(), e);
            throw new AuthenticationServiceException("Invalid request payload");
        }
    }

    private NeedUser findUserByIdentifier(String identifier) {
        return needUserRepository.findByEmail(identifier)
                .or(() -> needUserRepository.findByPhone(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {
        String userIdStr = authResult.getName();
        log.info("Looking up user by ID: {}", userIdStr);

        UUID userId = UUID.fromString(userIdStr);
        NeedUser user = needUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        log.info("Authenticated request from userId={} (role={}, email={}, phone={})",
                user.getId(), user.getRole(), user.getEmail(), user.getPhone());

        var token = tokenProvider.accessToken(user, authResult);
        var result = getResponse(user, token);

        writeJsonResponse(response, HttpServletResponse.SC_OK, result);
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    private static ApiResponse getResponse(NeedUser user, String token) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("role", user.getRoleAsAuthority());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());

        return new ApiResponse(true, Map.of(
                "access_token", token,
                "user", userData
        ), "Login successful");
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String errorMessage;

        if (failed instanceof InternalAuthenticationServiceException && failed.getCause() != null) {
            errorMessage = failed.getCause().getMessage();
        } else {
            errorMessage = switch (failed.getClass().getSimpleName()) {
                case "BadCredentialsException" -> "Invalid credentials.";
                case "UsernameNotFoundException" -> "User not found.";
                case "DisabledException" -> failed.getMessage();
                default -> "Authentication failed.";
            };
        }

        log.warn("Authentication failed: {}", errorMessage);
        var errorResponse = new ApiErrorResponse(false, null, errorMessage, ErrorType.ERROR);
        writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }

    private void writeJsonResponse(HttpServletResponse response, int status, Object body) {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            log.error("Error writing JSON response: {}", e.getMessage(), e);
        }
    }
}

