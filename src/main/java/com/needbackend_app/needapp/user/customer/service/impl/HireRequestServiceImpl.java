package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.HireRequestCreateRequest;
import com.needbackend_app.needapp.user.customer.dto.HireRequestResponse;
import com.needbackend_app.needapp.user.customer.model.HireRequest;
import com.needbackend_app.needapp.user.customer.repository.HireRequestRepository;
import com.needbackend_app.needapp.user.customer.service.HireRequestService;
import com.needbackend_app.needapp.user.enums.HireStatus;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HireRequestServiceImpl implements HireRequestService {

    private final HireRequestRepository hireRepo;
    private final NeedUserRepository userRepo;

    @Override
    public HireRequestResponse createHireRequest(UUID customerId, HireRequestCreateRequest request) {
        NeedUser customer = userRepo.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        NeedUser expert = userRepo.findById(request.expertId())
                .orElseThrow(() -> new EntityNotFoundException("Expert not found"));

        HireRequest hire = new HireRequest();
        hire.setCustomer(customer);
        hire.setExpert(expert);
        hire.setServiceDescription(request.serviceDescription());

        hireRepo.save(hire);

        return mapToResponse(hire);
    }

    @Override
    public void cancelHireRequest(UUID customerId, UUID requestId) throws AccessDeniedException {
        HireRequest hire = hireRepo.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (!hire.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You can only cancel your own request");
        }

        if (hire.getStatus() != HireStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }

        hire.setStatus(HireStatus.CANCELLED);
        hireRepo.save(hire);
    }

    @Override
    public List<HireRequestResponse> getMyRequests(UUID customerId) {
        return hireRepo.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private HireRequestResponse mapToResponse(HireRequest hire) {
        return new HireRequestResponse(
                hire.getId(),
                hire.getExpert().getId(),
                hire.getExpert().getFirstName() + " " + hire.getExpert().getLastName(),
                hire.getServiceDescription(),
                hire.getStatus(),
                hire.getCreatedAt()
        );
    }
}

