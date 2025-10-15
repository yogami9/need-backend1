package com.needbackend_app.needapp.ai.controller;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.dto.ChatSupportRequest;
import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/ai")
@RequiredArgsConstructor
@Tag(name = "Customer AI", description = "AI features for customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerAIController {

    private final AIAssistantService aiAssistantService;

    @PostMapping("/help-with-service")
    @Operation(summary = "Get AI help to identify needed services")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CategoryRecommendationResponse> getServiceHelp(
            @Valid @RequestBody CategoryRecommendationRequest request) {
        CategoryRecommendationResponse response = aiAssistantService.recommendCategories(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask-question")
    @Operation(summary = "Ask AI assistant a question")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AIResponse> askQuestion(
            @Valid @RequestBody ChatSupportRequest request) {
        AIResponse response = aiAssistantService.provideChatSupport(
            request.message(), 
            "Customer support context"
        );
        return ResponseEntity.ok(response);
    }
}