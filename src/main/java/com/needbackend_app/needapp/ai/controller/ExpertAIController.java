package com.needbackend_app.needapp.ai.controller;

import com.needbackend_app.needapp.ai.dto.ServiceDescriptionRequest;
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
@RequestMapping("/expert/ai")
@RequiredArgsConstructor
@Tag(name = "Expert AI", description = "AI features for experts")
@SecurityRequirement(name = "bearerAuth")
public class ExpertAIController {

    private final AIAssistantService aiAssistantService;

    @PostMapping("/enhance-profile")
    @Operation(summary = "Generate enhanced profile description")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<AIResponse> enhanceProfile(
            @Valid @RequestBody ServiceDescriptionRequest request) {
        AIResponse response = aiAssistantService.generateServiceDescription(
            request.category(), 
            request.subCategories(), 
            request.additionalDetails()
        );
        return ResponseEntity.ok(response);
    }
}