package com.needbackend_app.needapp.user.expert.service.impl;

import com.needbackend_app.needapp.user.expert.dto.ExpertNotificationDTO;
import com.needbackend_app.needapp.user.expert.model.ExpertNotification;
import com.needbackend_app.needapp.user.expert.repository.ExpertNotificationRepository;
import com.needbackend_app.needapp.user.expert.service.OfflineNotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfflineNotificationDispatcherImpl implements OfflineNotificationDispatcher {

    private final ExpertNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void dispatchUnreadNotifications(UUID expertId) {
        var unread = notificationRepository.findByExpertIdAndIsReadFalse(expertId);

        for (ExpertNotification notification : unread) {
            var dto = new ExpertNotificationDTO(
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getCreatedAt()
            );
            messagingTemplate.convertAndSendToUser(expertId.toString(), "/queue/notifications", dto);
        }

    }
}
