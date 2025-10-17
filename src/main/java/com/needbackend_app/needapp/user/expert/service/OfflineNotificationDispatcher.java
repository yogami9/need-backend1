package com.needbackend_app.needapp.user.expert.service;

import java.util.UUID;

public interface OfflineNotificationDispatcher {

    public void dispatchUnreadNotifications(UUID expertId);
}
