package com.needbackend_app.needapp.user.customer.dto;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "records")
public record RecordDocument(
        @Id UUID id,
        String name,
        String description,
        String name_autocomplete,
        String description_autocomplete,
        String type,
        UUID categoryId,
        String autocomplete_synonym
        ) {
}
