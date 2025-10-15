package com.needbackend_app.needapp.user.expert.service;

import com.needbackend_app.needapp.user.customer.dto.NewUserRequestEvent;

public interface ExpertNotificationListenerService {

    public void onNewRequest(NewUserRequestEvent event);
}
