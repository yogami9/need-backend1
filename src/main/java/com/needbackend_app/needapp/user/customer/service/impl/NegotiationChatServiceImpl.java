package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.MessageDto;
import com.needbackend_app.needapp.user.customer.dto.NegotiationMessageDto;
import com.needbackend_app.needapp.user.customer.model.HireRequest;
import com.needbackend_app.needapp.user.customer.repository.HireRequestRepository;
import com.needbackend_app.needapp.user.customer.service.NegotiationChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NegotiationChatServiceImpl implements NegotiationChatService {

    private final HireRequestRepository hireRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void processChatMessage(UUID requestId, MessageDto message, Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.warn("Unauthorized WebSocket access attempt");
            return;
        }

        UUID senderId = UUID.fromString(principal.getName());
        HireRequest hireRequest = hireRequestRepository.findById(requestId).orElse(null);

        if (hireRequest == null) {
            log.warn("Hire request not found: {}", requestId);
            return;
        }

        UUID customerId = hireRequest.getCustomer().getId();
        UUID expertId = hireRequest.getExpert().getId();

        if (!senderId.equals(customerId) && !senderId.equals(expertId)) {
            log.warn("User {} not authorized to chat on request {}", senderId, requestId);
            return;
        }

        NegotiationMessageDto negotiationMessage = new NegotiationMessageDto(
                requestId,
                senderId,
                message.content(),
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/hire/" + requestId, negotiationMessage);
    }
}

