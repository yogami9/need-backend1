package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.dto.CustomerServiceRequestDTO;
import com.needbackend_app.needapp.user.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/smart")
@RequiredArgsConstructor
@Tag(name = "Smart Customer Features", description = "AI-enhanced customer features")
@SecurityRequirement(name = "bearerAuth")
public class AIEnhancedCustomerController {

    private final AIAssistantService aiAssistantService;

    @PostMapping("/describe-need")
    @Operation(summary = "Describe what you need and get category suggestions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CategoryRecommendationResponse> describeNeed(
            @Valid @RequestBody CategoryRecommendationRequest request) {
        CategoryRecommendationResponse response = aiAssistantService.recommendCategories(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service-tips")
    @Operation(summary = "Get AI-powered tips for finding services")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getServiceTips(@RequestParam String category) {
        com.needbackend_app.needapp.ai.dto.AIResponse aiResponse = aiAssistantService.provideChatSupport(
            "Give me tips for finding a good " + category + " service provider",
            "Customer looking for service selection advice"
        );
        
        ApiResponse response = new ApiResponse(
            aiResponse.success(),
            aiResponse.content(),
            aiResponse.message()
        );
        
        return ResponseEntity.ok(response);
    }
}