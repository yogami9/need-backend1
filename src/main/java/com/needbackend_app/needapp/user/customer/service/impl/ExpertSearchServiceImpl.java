package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.util.ExpertProfileSpecification;
import com.needbackend_app.needapp.user.customer.dto.ExpertProfilePublicResponse;
import com.needbackend_app.needapp.user.customer.dto.ExpertSearchFilter;
import com.needbackend_app.needapp.user.customer.repository.CustomerExpertProfileRepository;
import com.needbackend_app.needapp.user.customer.service.ExpertSearchService;
import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpertSearchServiceImpl implements ExpertSearchService {

    private final CustomerExpertProfileRepository expertProfileRepository;

    @Override
    public List<ExpertProfilePublicResponse> searchExperts(ExpertSearchFilter filter) {
        List<ExpertProfilePublicResponse> results =expertProfileRepository.findAll(ExpertProfileSpecification.filterExperts(filter))
                .stream()
                .map(this::mapToResponse)
                .toList();
        log.info("Found {} matching experts", results.size());
        return results;
    }

    @Override
    public ExpertProfilePublicResponse getExpertProfile(UUID expertId) {
        ExpertProfile expert = expertProfileRepository.findById(expertId)
                .filter(e -> e.getApprovalStatus() == ApprovalStatus.APPROVED)
                .orElseThrow(() -> new EntityNotFoundException("Expert not found or not approved"));

        return mapToResponse(expert);
    }

    private ExpertProfilePublicResponse mapToResponse(ExpertProfile expert) {
        NeedUser user = expert.getNeedUser();
        String fullName = user.getFirstName() + " " + user.getLastName();

        List<String> subCategoryNames = expert.getSubCategories()
                .stream()
                .map(sub -> sub.getName())
                .toList();

        return new ExpertProfilePublicResponse(
                expert.getId(),
                fullName,
                expert.getCategory(),
                subCategoryNames,
                user.getProfile() != null ? user.getProfile().getAvatarUrl() : null,
                expert.getPortfolioUrl(),
                user.getState(),
                expert.getYearsOfExperience(),
                0.0 // TODO: Replace with actual rating logic later
        );
    }
}

