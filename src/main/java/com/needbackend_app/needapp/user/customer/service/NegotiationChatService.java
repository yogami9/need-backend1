package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.MessageDto;

import java.security.Principal;
import java.util.UUID;

public interface NegotiationChatService {
    void processChatMessage(UUID requestId, MessageDto message, Principal principal);
}
