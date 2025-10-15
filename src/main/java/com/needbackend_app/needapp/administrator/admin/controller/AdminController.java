package com.needbackend_app.needapp.administrator.admin.controller;

import com.needbackend_app.needapp.administrator.admin.dto.AdminCreateRequest;
import com.needbackend_app.needapp.administrator.admin.service.AdminCreateService;
import com.needbackend_app.needapp.user.util.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for managing Admin users")
public class AdminController {

    private final AdminCreateService adminCreateService;

    @Operation(summary = "Create a new Admin", description = "Only SuperAdmins can create new Admin users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        com.needbackend_app.needapp.user.util.ApiResponse response = adminCreateService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
