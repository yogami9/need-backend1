package com.needbackend_app.needapp.user.expert.service.impl;

import com.needbackend_app.needapp.user.customer.dto.NewUserRequestEvent;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import com.needbackend_app.needapp.user.expert.repository.ExpertProfileRepository;
import com.needbackend_app.needapp.user.expert.service.ExpertNotificationListenerService;
import com.needbackend_app.needapp.user.expert.service.ExpertNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpertNotificationListenerServiceImpl implements ExpertNotificationListenerService {

    private final ExpertProfileRepository expertRepository;
    private final ExpertNotificationService notificationService;

    @Override
    @EventListener
    public void onNewRequest(NewUserRequestEvent event) {
        UUID subCatId = event.requestDTO().subCategoryId();
        List<ExpertProfile> experts = expertRepository.findAllBySubcategoryId(subCatId);
        System.out.println("📢Matched experts for subcategory " + subCatId + ": " + experts.size());


        for (ExpertProfile expert : experts) {
            notificationService.SendNotificationToExpert(
                    expert.getId().toString(),
                    "New Customer Request",
                    "A new request was posted: " + event.requestDTO().description()
            );
        }
    }
}
