package com.needbackend_app.needapp.user.expert.controller;

import com.needbackend_app.needapp.user.expert.dto.ExpertApprovalResponse;
import com.needbackend_app.needapp.user.expert.service.ExpertApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/experts")
@RequiredArgsConstructor
@Tag(name = "Admin - Expert Approval", description = "Admin actions on Experts")
public class ExpertApprovalController {

    private final ExpertApprovalService approvalService;

    @Operation(summary = "Approve an expert by phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expert approved successfully"),
            @ApiResponse(responseCode = "404", description = "Expert not found"),
            @ApiResponse(responseCode = "409", description = "Expert already approved")
    })
    @PatchMapping("/{phone}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExpertApprovalResponse> approveExpert(@PathVariable String phone) {
        ExpertApprovalResponse response = approvalService.approveExpertByPhone(phone);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Reject an expert by phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expert rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Expert not found"),
            @ApiResponse(responseCode = "409", description = "Expert already rejected")
    })
    @PatchMapping("/{phone}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExpertApprovalResponse> rejectExpert(@PathVariable String phone) {
        ExpertApprovalResponse response = approvalService.rejectExpertByPhone(phone);
        return ResponseEntity.ok(response);
    }

}


