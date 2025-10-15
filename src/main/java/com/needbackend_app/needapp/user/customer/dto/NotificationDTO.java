package com.needbackend_app.needapp.user.customer.dto;

import java.time.LocalDateTime;

public record NotificationDTO(String title, String message, LocalDateTime timestamp) {
}
