package com.needbackend_app.needapp.user.expert.service.impl;

import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.expert.dto.ExpertApprovalResponse;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import com.needbackend_app.needapp.user.expert.repository.ExpertProfileRepository;
import com.needbackend_app.needapp.user.expert.service.ExpertApprovalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpertApprovalServiceImpl implements ExpertApprovalService {

    private final ExpertProfileRepository expertProfileRepository;

    @Override
    public ExpertApprovalResponse approveExpertByPhone(String phone) {
        ExpertProfile expert = expertProfileRepository.findByNeedUser_Phone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Expert with phone " + phone + " not found."));

        ApprovalStatus previousStatus = expert.getApprovalStatus();

        if (previousStatus == ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Expert is already approved.");
        }

        expert.setApprovalStatus(ApprovalStatus.APPROVED);
        expertProfileRepository.save(expert);

        return new ExpertApprovalResponse(
                expert.getId(),
                previousStatus,
                ApprovalStatus.APPROVED,
                "Expert has been approved successfully."
        );
    }

    @Override
    public ExpertApprovalResponse rejectExpertByPhone(String phone) {
        ExpertProfile expert = expertProfileRepository.findByNeedUser_Phone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Expert with phone " + phone + " not found."));

        ApprovalStatus previousStatus = expert.getApprovalStatus();

        if (previousStatus == ApprovalStatus.REJECTED) {
            throw new IllegalStateException("Expert is already rejected.");
        }

        expert.setApprovalStatus(ApprovalStatus.REJECTED);
        expertProfileRepository.save(expert);

        return new ExpertApprovalResponse(
                expert.getId(),
                previousStatus,
                ApprovalStatus.REJECTED,
                "Expert has been rejected successfully."
        );
    }
}


