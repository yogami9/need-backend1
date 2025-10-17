package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.model.CustomerServiceRequest;
import com.needbackend_app.needapp.user.customer.repository.CustomerServiceRequestRepository;
import com.needbackend_app.needapp.user.customer.service.ExpertUpdateStatusService;
import com.needbackend_app.needapp.user.customer.service.NotificationService;
import com.needbackend_app.needapp.user.enums.RequestStatus;
import com.needbackend_app.needapp.user.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/expert/update")
@RequiredArgsConstructor
public class ExpertStatusUpdateController {

    private final ExpertUpdateStatusService updateStatusService;

    @Operation(summary = "expert updates job status")
    @PutMapping("/request/{requestId}/status")
    public ResponseEntity<ApiResponse> updateRequestStatus (
            @PathVariable UUID requestId,
            @RequestParam RequestStatus status
    ) {

        ApiResponse response = updateStatusService.updateRequestStatus(requestId, status);
        return ResponseEntity.ok(response);
    }
}

