package com.needbackend_app.needapp.user.expert.service;

import java.util.UUID;

public interface ExpertNotificationService {

    public void SendNotificationToExpert(String expertId, String title, String message);

    public void markAsRead(UUID notificationId);
}

