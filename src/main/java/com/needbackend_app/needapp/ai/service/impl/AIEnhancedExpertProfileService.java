package com.needbackend_app.needapp.ai.service.impl;

import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.auth.util.AuthenticatedUserProvider;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import com.needbackend_app.needapp.user.expert.repository.ExpertProfileRepository;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIEnhancedExpertProfileService {

    private final ExpertProfileRepository expertProfileRepository;
    private final AIAssistantService aiAssistantService;
    private final AuthenticatedUserProvider userProvider;

    public ApiResponse generateEnhancedProfileDescription() {
        try {
            NeedUser currentUser = userProvider.getCurrentUser();
            ExpertProfile profile = currentUser.getExpertProfile();
            
            if (profile == null) {
                return new ApiResponse(false, null, "Expert profile not found");
            }

            List<String> subCategoryNames = profile.getSubCategories().stream()
                .map(sub -> sub.getName())
                .collect(Collectors.toList());

            String additionalDetails = String.format(
                "Years of experience: %d, Portfolio: %s", 
                profile.getYearsOfExperience(),
                profile.getPortfolioUrl() != null ? "Available" : "Not provided"
            );

            AIResponse aiResponse = aiAssistantService.generateServiceDescription(
                profile.getCategory(),
                subCategoryNames,
                additionalDetails
            );

            if (aiResponse.success()) {
                return new ApiResponse(true, Map.of(
                    "generatedDescription", aiResponse.content(),
                    "category", profile.getCategory(),
                    "subCategories", subCategoryNames
                ), "Enhanced profile description generated successfully");
            } else {
                return new ApiResponse(false, null, "Failed to generate description: " + aiResponse.message());
            }

        } catch (Exception e) {
            log.error("Error generating enhanced profile: {}", e.getMessage(), e);
            return new ApiResponse(false, null, "An error occurred while generating profile description");
        }
    }
}