package com.needbackend_app.needapp.user.expert.dto;

import com.needbackend_app.needapp.user.enums.ApprovalStatus;

import java.util.UUID;

public record ExpertApprovalResponse(
        UUID expertId,
        ApprovalStatus previousStatus,
        ApprovalStatus newStatus,
        String message
) {}

