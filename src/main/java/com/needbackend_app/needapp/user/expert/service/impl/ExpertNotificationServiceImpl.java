package com.needbackend_app.needapp.user.expert.service.impl;

import com.needbackend_app.needapp.user.expert.dto.ExpertNotificationDTO;
import com.needbackend_app.needapp.user.expert.model.ExpertNotification;
import com.needbackend_app.needapp.user.expert.repository.ExpertNotificationRepository;
import com.needbackend_app.needapp.user.expert.service.ExpertNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpertNotificationServiceImpl implements ExpertNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ExpertNotificationRepository notificationRepository;

    @Override
    public void SendNotificationToExpert(String expertId, String title, String message) {

        UUID expertUUID = UUID.fromString(expertId);

        ExpertNotification entity = ExpertNotification.builder()
                .expertId(expertUUID)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(entity);

        ExpertNotificationDTO notification = new ExpertNotificationDTO(
                title,
                message,
                entity.getCreatedAt()
        );
        System.out.println("sending to expertId: " + expertId);
        messagingTemplate.convertAndSendToUser(expertId, "queue/notifications", notification);
    }


    @Override
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
