package com.needbackend_app.needapp.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.needbackend_app.needapp.ai.config.AIProperties;
import com.needbackend_app.needapp.ai.dto.AIRequest;
import com.needbackend_app.needapp.ai.dto.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HuggingFaceService {

    private final RestTemplate huggingFaceRestTemplate;
    private final AIProperties aiProperties;
    private final AIResponseMonitor responseMonitor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIResponse generateCategoryRecommendation(String prompt) {
        String modelName = aiProperties.getHuggingface().getModels().getCategoryRecommendation();
        return callModel(modelName, prompt, "categorize: ");
    }

    public AIResponse generateChatResponse(String prompt) {
        String modelName = aiProperties.getHuggingface().getModels().getChatSupport();
        return callModel(modelName, prompt, "answer question: ");
    }

    public AIResponse generateServiceDescription(String prompt) {
        String modelName = aiProperties.getHuggingface().getModels().getServiceDescription();
        return callModel(modelName, prompt, "generate professional description: ");
    }

    public AIResponse moderateContent(String content) {
        String modelName = aiProperties.getHuggingface().getModels().getContentModeration();
        return callModel(modelName, content, "");
    }

    // ADD THIS METHOD - This was missing!
    public AIResponse generateContentWithContext(AIRequest request) {
        if (!aiProperties.getHuggingface().isEnabled()) {
            return new AIResponse(false, "AI service is disabled", null);
        }

        if (request == null || request.prompt() == null || request.prompt().trim().isEmpty()) {
            return new AIResponse(false, "Prompt cannot be empty", null);
        }

        try {
            // Build context-aware prompt
            StringBuilder fullPrompt = new StringBuilder();
            
            if (request.systemContext() != null && !request.systemContext().isEmpty()) {
                fullPrompt.append("System: ").append(request.systemContext()).append("\n\n");
            }
            
            if (request.userContext() != null && !request.userContext().isEmpty()) {
                fullPrompt.append("Context: ").append(request.userContext()).append("\n\n");
            }
            
            fullPrompt.append("User: ").append(request.prompt());
            
            if (request.constraints() != null && !request.constraints().isEmpty()) {
                fullPrompt.append("\n\nConstraints:\n");
                for (String constraint : request.constraints()) {
                    fullPrompt.append("- ").append(constraint).append("\n");
                }
            }

            // Use the specified model or default to chat support
            String modelName = request.model() != null ? 
                request.model() : 
                aiProperties.getHuggingface().getModels().getChatSupport();
            
            return callModel(modelName, fullPrompt.toString(), "");
            
        } catch (Exception e) {
            log.error("Error generating content with context: {}", e.getMessage(), e);
            return new AIResponse(false, "Failed to generate content", null);
        }
    }

    private AIResponse callModel(String modelName, String prompt, String prefix) {
        if (!aiProperties.getHuggingface().isEnabled()) {
            return new AIResponse(false, "AI service is disabled", null);
        }

        if (prompt == null || prompt.trim().isEmpty()) {
            return new AIResponse(false, "Prompt cannot be empty", null);
        }

        try {
            String fullModelPath = aiProperties.getHuggingface().getUsername() + "/" + modelName;
            String url = aiProperties.getHuggingface().getApiUrl() + "/" + fullModelPath;

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", prefix + prompt);
            requestBody.put("parameters", Map.of(
                "max_new_tokens", 250,
                "temperature", 0.7,
                "top_p", 0.9,
                "do_sample", true
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.debug("Calling HuggingFace model: {} with prompt: {}", fullModelPath, 
                prompt.length() > 100 ? prompt.substring(0, 100) + "..." : prompt);

            ResponseEntity<String> response = huggingFaceRestTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String generatedText = extractGeneratedText(response.getBody());
                
                if (generatedText != null && !generatedText.trim().isEmpty()) {
                    log.info("Successfully generated content (length: {} chars)", generatedText.length());
                    return new AIResponse(true, "Content generated successfully", generatedText.trim());
                } else {
                    log.warn("HuggingFace returned empty response");
                    return new AIResponse(false, "Empty response from AI service", null);
                }
            } else {
                log.warn("HuggingFace API returned status: {}", response.getStatusCode());
                return new AIResponse(false, "AI service returned unexpected status", null);
            }

        } catch (Exception e) {
            log.error("Error calling HuggingFace model: {}", e.getMessage(), e);
            
            if (e.getMessage() != null) {
                String errorMessage = e.getMessage().toLowerCase();
                if (errorMessage.contains("timeout")) {
                    return new AIResponse(false, "AI service timeout. Please try again.", null);
                } else if (errorMessage.contains("429")) {
                    return new AIResponse(false, "AI service rate limit exceeded. Please try again later.", null);
                }
            }
            
            return new AIResponse(false, "AI service temporarily unavailable", null);
        }
    }

    private String extractGeneratedText(String responseBody) {
        try {
            // HuggingFace returns array of results
            var jsonNode = objectMapper.readTree(responseBody);
            
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                var firstResult = jsonNode.get(0);
                if (firstResult.has("generated_text")) {
                    return firstResult.get("generated_text").asText();
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error parsing HuggingFace response: {}", e.getMessage());
            return null;
        }
    }

    public boolean isServiceHealthy() {
        try {
            AIResponse response = generateChatResponse("Hello, are you working?");
            return response.success() && response.content() != null && !response.content().isBlank();
        } catch (Exception e) {
            log.error("AI health check failed: {}", e.getMessage());
            return false;
        }
    }
}