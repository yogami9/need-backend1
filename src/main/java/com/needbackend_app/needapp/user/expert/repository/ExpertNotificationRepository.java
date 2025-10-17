package com.needbackend_app.needapp.user.expert.repository;

import com.needbackend_app.needapp.user.customer.dto.NotificationDTO;
import com.needbackend_app.needapp.user.expert.model.ExpertNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpertNotificationRepository extends JpaRepository<ExpertNotification, UUID> {

    List<ExpertNotification> findByExpertIdAndIsReadFalse(UUID expertId);
}
