package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.dto.CustomerServiceRequestDTO;
import com.needbackend_app.needapp.user.customer.dto.CustomerServiceResponseDTO;
import com.needbackend_app.needapp.user.customer.dto.NewUserRequestEvent;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.CustomerServiceRequest;
import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.CustomerServiceRequestRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import com.needbackend_app.needapp.user.customer.service.CustomerServiceRequestService;
import com.needbackend_app.needapp.user.enums.RequestStatus;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import com.needbackend_app.needapp.user.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceRequestServiceImpl implements CustomerServiceRequestService {

    private final CustomerServiceRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final NeedUserRepository needUserRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired(required = false)
    private AIAssistantService aiAssistantService;

    @Override
    public ApiResponse createRequest(CustomerServiceRequestDTO requestDTO) {
        try {
            // Validate category and subcategory exist
            Category category = categoryRepository.findById(requestDTO.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            SubCategory subCategory = subCategoryRepository.findById(requestDTO.subCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));

            // Get current authenticated customer
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerId = authentication.getName();

            NeedUser currentCustomer = needUserRepository.findById(UUID.fromString(customerId))
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Create and save the service request
            CustomerServiceRequest request = new CustomerServiceRequest();
            request.setCategory(category);
            request.setSubCategory(subCategory);
            request.setDescription(requestDTO.description());
            request.setStatus(RequestStatus.PENDING);
            request.setCreatedAt(LocalDateTime.now());
            request.setCustomer(currentCustomer);

            CustomerServiceRequest savedRequest = requestRepository.save(request);

            // Build response data
            Map<String, Object> responseData = new HashMap<>();
            CustomerServiceResponseDTO mainResponse = new CustomerServiceResponseDTO(
                savedRequest.getId(),
                savedRequest.getDescription(),
                savedRequest.getCategory().getName(),
                savedRequest.getSubCategory().getName(),
                savedRequest.getStatus(),
                savedRequest.getCreatedAt()
            );
            responseData.put("request", mainResponse);

            // Add AI enhancement if service is available
            if (aiAssistantService != null) {
                try {
                    CategoryRecommendationResponse aiSuggestion = aiAssistantService.recommendCategories(
                        new CategoryRecommendationRequest(requestDTO.description())
                    );
                    
                    if (aiSuggestion.success()) {
                        Map<String, Object> aiEnhancements = new HashMap<>();
                        aiEnhancements.put("recommendedCategory", aiSuggestion.recommendedCategory());
                        aiEnhancements.put("recommendedSubCategories", aiSuggestion.recommendedSubCategories());
                        aiEnhancements.put("confidence", "AI-suggested alternatives based on your description");
                        
                        responseData.put("aiSuggestions", aiEnhancements);
                        log.info("AI suggestions added for request: {}", savedRequest.getId());
                    }
                } catch (Exception e) {
                    log.warn("AI enhancement failed for request {}: {}", savedRequest.getId(), e.getMessage());
                    // Continue without AI enhancement - core functionality preserved
                }
            }

            // Publish event for expert notifications
            eventPublisher.publishEvent(new NewUserRequestEvent(requestDTO));
            log.info("Service request created successfully: {}", savedRequest.getId());

            return new ApiResponse(true, responseData, "Request submitted successfully");

        } catch (RuntimeException e) {
            log.error("Failed to create service request: {}", e.getMessage(), e);
            return new ApiResponse(false, null, "Failed to submit request: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating service request: {}", e.getMessage(), e);
            return new ApiResponse(false, null, "An unexpected error occurred while processing your request");
        }
    }

    @Override
    public List<CustomerServiceResponseDTO> getAllequest() {
        try {
            return requestRepository.findAll().stream()
                    .map(request -> new CustomerServiceResponseDTO(
                            request.getId(),
                            request.getDescription(),
                            request.getCategory().getName(),
                            request.getSubCategory().getName(),
                            request.getStatus(),
                            request.getCreatedAt()
                    ))
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving all requests: {}", e.getMessage(), e);
            return List.of();
        }
    }
}