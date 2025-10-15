package com.needbackend_app.needapp.user.expert.controller;

import com.needbackend_app.needapp.user.expert.dto.ExpertSignupRequest;
import com.needbackend_app.needapp.user.expert.service.ExpertSignUpService;
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
@Tag(name = "Expert Management", description = "Endpoints for managing Expert users")
public class ExpertSignUpController {

    private final ExpertSignUpService expertSignUpService;


    @PostMapping("/expert")
    @Operation(summary = "Register a new expert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expert registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<?> registerExpert(@Valid @RequestBody ExpertSignupRequest request) {
        com.needbackend_app.needapp.user.util.ApiResponse response = expertSignUpService.registerExpert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

