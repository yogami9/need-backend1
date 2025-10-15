package com.needbackend_app.needapp.user.expert.service;

import com.needbackend_app.needapp.user.expert.dto.ExpertApprovalResponse;

public interface ExpertApprovalService {
    ExpertApprovalResponse approveExpertByPhone(String phone);

    ExpertApprovalResponse rejectExpertByPhone(String phone);
}
