package com.needbackend_app.needapp.auth.websocketauth;

import com.needbackend_app.needapp.auth.jwt.JwtService;
import com.needbackend_app.needapp.user.customer.service.NotificationService;
import com.needbackend_app.needapp.user.expert.service.ExpertNotificationService;
import com.needbackend_app.needapp.user.expert.service.OfflineNotificationDispatcher;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final NeedUserRepository needUserRepository;
    private final ApplicationContext context;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }

            token = token.substring(7);

            if (!jwtService.isTokenValid(token)) {
                throw new SecurityException("Invalid or expired token");
            }

            String userId = String.valueOf(jwtService.extractUserId(token));
            log.info("WebSocket connection authenticated for userId {}", userId);

            NeedUser user = needUserRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            accessor.setUser(new StompPrincipal(user.getId().toString()));

            OfflineNotificationDispatcher dispatcher = context.getBean(OfflineNotificationDispatcher.class);
            dispatcher.dispatchUnreadNotifications(UUID.fromString(userId));
        }
        return message;
    }
}

