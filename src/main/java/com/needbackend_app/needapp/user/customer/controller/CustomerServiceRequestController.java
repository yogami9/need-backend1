package com.needbackend_app.needapp.user.customer.controller;

import com.needbackend_app.needapp.user.customer.dto.*;
import com.needbackend_app.needapp.user.customer.service.CategoryService;
import com.needbackend_app.needapp.user.customer.service.CustomerServiceRequestService;
import com.needbackend_app.needapp.user.customer.service.SubCategoryService;
import com.needbackend_app.needapp.user.customer.service.SuggestService;
import com.needbackend_app.needapp.user.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer")
@Tag(name = "Customer Management", description = "Endpoint for managing customer request services")
@RequiredArgsConstructor
public class CustomerServiceRequestController {

    private final CustomerServiceRequestService requestService;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final SuggestService suggestService;

    @Operation(summary = "Search for all categories")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Search category by Id")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID categoryId) {
        CategoryDTO category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Search subcategories")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategories(@PathVariable UUID categoryId) {
        List<SubCategoryDTO> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @Operation(summary = "Make a request for service")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/request")
    public ResponseEntity<ApiResponse> createServiceRequest (@RequestBody CustomerServiceRequestDTO requestDTO) {
        ApiResponse response = requestService.createRequest(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "View List of all previous requested services")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/all-request")
    public ResponseEntity<List<CustomerServiceResponseDTO>> getAllRequest() {
        return ResponseEntity.ok(requestService.getAllequest());
    }

    @Operation(summary = "Auto-suggest categories and subcategories based on text input")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/suggest")
    public ResponseEntity<SuggestionResponseDTO> suggestCategoryAndSubCategory(@RequestParam String query,
                                                                               @RequestParam(required = false) UUID categoryId){
        return ResponseEntity.ok(suggestService.suggest(query, categoryId));
    }
}

