package com.needbackend_app.needapp.user.expert.service.impl;

import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.user.expert.dto.ExpertSignupRequest;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import com.needbackend_app.needapp.user.expert.repository.ExpertProfileRepository;
import com.needbackend_app.needapp.user.expert.service.ExpertSignUpService;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import com.needbackend_app.needapp.user.util.ApiResponse;
import com.needbackend_app.needapp.user.util.PasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertSignUpServiceImpl implements ExpertSignUpService {

    private final NeedUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ExpertProfileRepository expertProfileRepository;
    
    @Autowired(required = false)
    private AIAssistantService aiAssistantService;

    @Override
    @Transactional
    public ApiResponse registerExpert(ExpertSignupRequest request) {
        try {
            // Validate passwords match
            PasswordValidation.validatePasswords(request.password(), request.confirmPassword());

            // Check if phone already exists
            if (userRepository.existsByPhone(request.phone())) {
                return new ApiResponse(false, null, "Phone number already exists");
            }

            // Content moderation using AI if available
            if (aiAssistantService != null) {
                String contentToModerate = buildContentForModeration(request);
                try {
                    AIResponse moderation = aiAssistantService.moderateContent(contentToModerate);
                    if (moderation.success() && moderation.content() != null) {
                        String moderationResult = moderation.content().toUpperCase();
                        if (moderationResult.contains("REJECTED")) {
                            log.warn("Expert registration rejected due to inappropriate content: {}", request.phone());
                            return new ApiResponse(false, null, "Registration contains inappropriate content. Please review and try again.");
                        }
                        if (moderationResult.contains("FLAGGED")) {
                            log.info("Expert registration flagged for review: {}", request.phone());
                            // Continue with registration but flag for admin review
                        }
                    }
                } catch (Exception e) {
                    log.warn("Content moderation failed for expert registration {}: {}", request.phone(), e.getMessage());
                    // Continue with registration if AI moderation fails
                }
            }

            // Validate category exists
            Category category = categoryRepository.findByNameIgnoreCase(request.category())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.category()));

            // Validate subcategories exist
            List<SubCategory> subCategories = subCategoryRepository.findByNameInIgnoreCase(request.subCategories());
            
            List<String> foundNames = subCategories.stream()
                    .map(SubCategory::getName)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            List<String> notFound = request.subCategories().stream()
                    .map(String::toLowerCase)
                    .filter(name -> !foundNames.contains(name))
                    .collect(Collectors.toList());

            if (!notFound.isEmpty()) {
                return new ApiResponse(false, null, "Subcategories not found: " + String.join(", ", notFound));
            }

            // Create new user
            NeedUser user = new NeedUser();
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setPhone(request.phone());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setRole(Role.EXPERT);

            // Create expert profile
            ExpertProfile profile = new ExpertProfile();
            profile.setCategory(category.getName());
            profile.setSubCategories(new HashSet<>(subCategories));
            profile.setYearsOfExperience(request.yearsOfExperience());
            profile.setPortfolioUrl(request.portfolioUrl());
            profile.setNationalId(request.nationalId());
            profile.setApprovalStatus(ApprovalStatus.PENDING); // Changed from APPROVED to PENDING for proper workflow
            profile.setNeedUser(user);

            // Set the profile relationship
            user.setExpertProfile(profile);

            // Save user (cascade will save profile)
            NeedUser savedUser = userRepository.save(user);
            log.info("Expert registered successfully: {} {} with phone {}", 
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getPhone());

            return new ApiResponse(true, null, "Expert registered successfully. Account pending admin approval.");

        } catch (IllegalArgumentException e) {
            log.warn("Invalid expert registration data: {}", e.getMessage());
            return new ApiResponse(false, null, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during expert registration: {}", e.getMessage(), e);
            return new ApiResponse(false, null, "Registration failed due to technical error. Please try again.");
        }
    }

    private String buildContentForModeration(ExpertSignupRequest request) {
        StringBuilder content = new StringBuilder();
        content.append("Name: ").append(request.firstName()).append(" ").append(request.lastName()).append("\n");
        content.append("Category: ").append(request.category()).append("\n");
        content.append("Subcategories: ").append(String.join(", ", request.subCategories())).append("\n");
        
        if (request.portfolioUrl() != null && !request.portfolioUrl().isBlank()) {
            content.append("Portfolio URL: ").append(request.portfolioUrl()).append("\n");
        }
        
        return content.toString();
    }
}