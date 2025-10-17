package com.needbackend_app.needapp.ai.controller;

import com.needbackend_app.needapp.ai.dto.*;
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
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "AI-powered features and assistance")
@SecurityRequirement(name = "bearerAuth")
public class AIAssistantController {

    private final AIAssistantService aiAssistantService;

    @PostMapping("/recommend-category")
    @Operation(summary = "Get AI-powered category recommendations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CategoryRecommendationResponse> recommendCategory(
            @Valid @RequestBody CategoryRecommendationRequest request) {
        CategoryRecommendationResponse response = aiAssistantService.recommendCategories(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-service-description")
    @Operation(summary = "Generate professional service description")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<AIResponse> generateServiceDescription(
            @Valid @RequestBody ServiceDescriptionRequest request) {
        AIResponse response = aiAssistantService.generateServiceDescription(
            request.category(), 
            request.subCategories(), 
            request.additionalDetails()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/moderate-content")
    @Operation(summary = "Moderate content for appropriateness")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<AIResponse> moderateContent(
            @Valid @RequestBody ContentModerationRequest request) {
        AIResponse response = aiAssistantService.moderateContent(request.content());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat-support")
    @Operation(summary = "Get AI-powered chat support")
    public ResponseEntity<AIResponse> getChatSupport(
            @Valid @RequestBody ChatSupportRequest request) {
        AIResponse response = aiAssistantService.provideChatSupport(
            request.message(), 
            request.context()
        );
        return ResponseEntity.ok(response);
    }
}
