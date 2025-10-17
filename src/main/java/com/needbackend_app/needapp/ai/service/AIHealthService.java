package com.needbackend_app.needapp.ai.service;

import com.needbackend_app.needapp.ai.dto.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIHealthService {

    private final HuggingFaceService huggingFaceService;

    public boolean isAIServiceHealthy() {
        try {
            AIResponse response = huggingFaceService.generateChatResponse("Hello, are you working?");
            return response.success() && response.content() != null && !response.content().isBlank();
        } catch (Exception e) {
            log.error("AI health check failed: {}", e.getMessage());
            return false;
        }
    }

    public String getDetailedHealthStatus() {
        try {
            AIResponse response = huggingFaceService.generateChatResponse("Respond with 'HEALTHY' if you're working properly.");
            if (response.success()) {
                return "AI Service: HEALTHY - " + response.content();
            } else {
                return "AI Service: UNHEALTHY - " + response.message();
            }
        } catch (Exception e) {
            return "AI Service: ERROR - " + e.getMessage();
        }
    }
}