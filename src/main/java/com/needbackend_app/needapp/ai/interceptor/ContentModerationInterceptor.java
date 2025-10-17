package com.needbackend_app.needapp.ai.interceptor;

import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentModerationInterceptor {

    private final AIAssistantService aiAssistantService;

    public boolean isContentAppropriate(String content) {
        try {
            AIResponse moderationResult = aiAssistantService.moderateContent(content);
            
            if (moderationResult.success() && moderationResult.content() != null) {
                String result = moderationResult.content().toUpperCase();
                return result.contains("APPROVED");
            }
            
            // If AI fails, default to allowing content (fail-safe)
            log.warn("Content moderation AI failed, defaulting to approval");
            return true;
            
        } catch (Exception e) {
            log.error("Error in content moderation: {}", e.getMessage(), e);
            return true; // Fail-safe: allow content if moderation fails
        }
    }
}