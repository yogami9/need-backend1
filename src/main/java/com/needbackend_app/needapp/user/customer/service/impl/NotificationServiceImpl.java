package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.NotificationDTO;
import com.needbackend_app.needapp.user.customer.service.NotificationService;
import com.needbackend_app.needapp.user.expert.dto.ExpertNotificationDTO;
import com.needbackend_app.needapp.user.expert.model.ExpertNotification;
import com.needbackend_app.needapp.user.expert.repository.ExpertNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void SendNotificationToCustomer(String userId, String title, String message) {

        NotificationDTO notification = new NotificationDTO(
                title,
                message,
                LocalDateTime.now()
        );
        System.out.println("📣Sending to userId: " + userId);
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);

    }


}
