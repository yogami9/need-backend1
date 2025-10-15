package com.needbackend_app.needapp.user.customer.service;

import java.util.UUID;

public interface NotificationService {
    public void SendNotificationToCustomer(String userId, String title, String message);

}
