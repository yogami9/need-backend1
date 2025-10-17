package com.needbackend_app.needapp.user.customer.repository;

import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CustomerExpertProfileRepository extends JpaRepository<ExpertProfile, UUID>, JpaSpecificationExecutor<ExpertProfile> {
    List<ExpertProfile> findByApprovalStatus(ApprovalStatus status);
}

