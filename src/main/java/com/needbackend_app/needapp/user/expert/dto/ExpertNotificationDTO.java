package com.needbackend_app.needapp.user.expert.dto;

import java.time.LocalDateTime;

public record ExpertNotificationDTO(String title, String message, LocalDateTime createdAt) {
}
