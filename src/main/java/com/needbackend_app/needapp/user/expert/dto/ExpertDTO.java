package com.needbackend_app.needapp.user.expert.dto;

import java.util.List;
import java.util.UUID;

public record ExpertDTO(UUID id, String name, List<UUID> subcategoryIds) {
}
