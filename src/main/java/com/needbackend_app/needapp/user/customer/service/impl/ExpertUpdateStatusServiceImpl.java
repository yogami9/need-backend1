package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.model.CustomerServiceRequest;
import com.needbackend_app.needapp.user.customer.repository.CustomerServiceRequestRepository;
import com.needbackend_app.needapp.user.customer.service.ExpertUpdateStatusService;
import com.needbackend_app.needapp.user.customer.service.NotificationService;
import com.needbackend_app.needapp.user.enums.RequestStatus;
import com.needbackend_app.needapp.user.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpertUpdateStatusServiceImpl implements ExpertUpdateStatusService {

    private final CustomerServiceRequestRepository requestRepository;
    private final NotificationService notificationService;

    @Override
    public ApiResponse updateRequestStatus(UUID requestId, RequestStatus status) {
        CustomerServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        requestRepository.save(request);

        try {
            notificationService.SendNotificationToCustomer(
                    request.getCustomer().getId().toString(),
                    "Service Request update",
                    "Your request is now: "+ status.name()
            );
        }catch (Exception e){
            log.error("Failed to send notification to customer with ID {}: {}", request.getCustomer().getId(), e.getMessage(), e);
        }
        return new ApiResponse(true, null, "Request status updated to: "+ status.name());
    }
}
