package com.needbackend_app.needapp.ai.service.impl;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.dto.CustomerServiceRequestDTO;
import com.needbackend_app.needapp.user.customer.service.CustomerServiceRequestService;
import com.needbackend_app.needapp.user.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIEnhancedCustomerServiceImpl {

    private final CustomerServiceRequestService baseService;
    private final AIAssistantService aiAssistantService;

    public ApiResponse createRequestWithAIAssistance(CustomerServiceRequestDTO requestDTO) {
        try {
            // First, let AI validate and enhance the request
            CategoryRecommendationResponse aiRecommendation = 
                aiAssistantService.recommendCategories(
                    new CategoryRecommendationRequest(requestDTO.description())
                );

            // Create the original request
            ApiResponse baseResponse = baseService.createRequest(requestDTO);

            // Enhance response with AI insights if successful
            if (baseResponse.success() && aiRecommendation.success()) {
                Map<String, Object> enhancedData = Map.of(
                    "originalRequest", baseResponse.data(),
                    "aiSuggestions", Map.of(
                        "recommendedCategory", aiRecommendation.recommendedCategory(),
                        "recommendedSubCategories", aiRecommendation.recommendedSubCategories()
                    )
                );
                
                return new ApiResponse(true, enhancedData, 
                    "Request created successfully with AI recommendations");
            }

            return baseResponse;
            
        } catch (Exception e) {
            log.warn("AI enhancement failed, falling back to basic service: {}", e.getMessage());
            return baseService.createRequest(requestDTO);
        }
    }
}