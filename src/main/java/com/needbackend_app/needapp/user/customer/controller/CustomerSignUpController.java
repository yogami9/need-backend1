package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.dto.CustomerSignupRequest;
import com.needbackend_app.needapp.user.customer.service.CustomerSignUpService;
import com.needbackend_app.needapp.user.customer.service.impl.CustomerSignUpServiceImpl;
import com.needbackend_app.needapp.user.util.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for managing customers users")
public class CustomerSignUpController {

    private final CustomerSignUpService customerSignupService;

    @PostMapping("/customer")
    @Operation(summary = "Register a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerSignupRequest request) {
        com.needbackend_app.needapp.user.util.ApiResponse response = customerSignupService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
