package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.auth.util.AuthenticatedUserProvider;
import com.needbackend_app.needapp.user.customer.dto.HireRequestCreateRequest;
import com.needbackend_app.needapp.user.customer.dto.HireRequestResponse;
import com.needbackend_app.needapp.user.customer.service.HireRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer/hire")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for managing customers users")
public class HireRequestController {

    private final HireRequestService hireRequestService;
    private final AuthenticatedUserProvider userProvider;

    @PostMapping
    @Operation(summary = "Send hire request to expert")
    public ResponseEntity<HireRequestResponse> hireExpert(@RequestBody HireRequestCreateRequest request) {
        UUID customerId = userProvider.getCurrentUserId();
        HireRequestResponse response = hireRequestService.createHireRequest(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{requestId}/cancel")
    @Operation(summary = "Cancel a hire request")
    public ResponseEntity<Void> cancelRequest(@PathVariable UUID requestId) throws AccessDeniedException {
        UUID customerId = userProvider.getCurrentUserId();
        hireRequestService.cancelHireRequest(customerId, requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-requests")
    @Operation(summary = "View all hire requests made by the customer")
    public ResponseEntity<List<HireRequestResponse>> myRequests() {
        UUID customerId = userProvider.getCurrentUserId();
        return ResponseEntity.ok(hireRequestService.getMyRequests(customerId));
    }
}

