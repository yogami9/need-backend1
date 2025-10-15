package com.needbackend_app.needapp.user.customer.repository;

import com.needbackend_app.needapp.user.customer.model.HireRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HireRequestRepository extends JpaRepository<HireRequest, UUID> {
    List<HireRequest> findByCustomerId(UUID customerId);
}

