package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.dto.ExpertProfilePublicResponse;
import com.needbackend_app.needapp.user.customer.dto.ExpertSearchFilter;
import com.needbackend_app.needapp.user.customer.service.ExpertSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers/experts")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for managing customers users")
public class ExpertSearchController {

    private final ExpertSearchService expertSearchService;

    @Operation(summary = "Search for experts")
    @PostMapping("/search")
    public ResponseEntity<List<ExpertProfilePublicResponse>> searchExperts(@RequestBody ExpertSearchFilter filter) {
        List<ExpertProfilePublicResponse> results = expertSearchService.searchExperts(filter);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "View an expert's public profile")
    @GetMapping("/{expertId}")
    public ResponseEntity<ExpertProfilePublicResponse> getExpertProfile(@PathVariable UUID expertId) {
        ExpertProfilePublicResponse response = expertSearchService.getExpertProfile(expertId);
        return ResponseEntity.ok(response);
    }
}

