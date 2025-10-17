package com.needbackend_app.needapp.user.customer.util;

import com.needbackend_app.needapp.user.customer.dto.ExpertSearchFilter;
import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpertProfileSpecification {

    public static Specification<ExpertProfile> filterExperts(ExpertSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("approvalStatus"), ApprovalStatus.APPROVED));

            if (filter.category() != null && !filter.category().isBlank()) {
                predicates.add(cb.equal(root.get("category"), filter.category()));}

            if (filter.subCategories() != null && !filter.subCategories().isEmpty()) {
                Join<ExpertProfile, String> subCatJoin = root.join("subCategories");
                predicates.add(subCatJoin.in(filter.subCategories()));}

            if (filter.location() != null && !filter.location().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("needUser").get("state")),
                        "%" + filter.location().toLowerCase() + "%"
                ));}

            if (filter.minExperience() != null) {
                predicates.add(cb.ge(root.get("yearsOfExperience"), filter.minExperience()));}

            if (filter.maxExperience() != null) {
                predicates.add(cb.le(root.get("yearsOfExperience"), filter.maxExperience()));}

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


