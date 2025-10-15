package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.dto.MessageDto;
import com.needbackend_app.needapp.user.customer.service.NegotiationChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NegotiationChatController {

    private final NegotiationChatService chatService;

    @MessageMapping("/hire/{requestId}/chat")
    public void handleChat(@DestinationVariable UUID requestId,
                           @Payload MessageDto message,
                           Principal principal) {
        chatService.processChatMessage(requestId, message, principal);
    }
}

