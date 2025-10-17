package com.needbackend_app.needapp.user.customer.dto;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

    @Document(indexName = "categories")
    public record CategoryDocumentDTO(@Id UUID id, String name, String description){
    }
