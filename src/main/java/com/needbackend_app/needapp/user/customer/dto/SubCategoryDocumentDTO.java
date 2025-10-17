package com.needbackend_app.needapp.user.customer.dto;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "subcategories")
public record SubCategoryDocumentDTO(@Id  UUID id, String name, String description, UUID categoryId) {
}
