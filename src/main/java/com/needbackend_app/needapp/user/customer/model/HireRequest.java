package com.needbackend_app.needapp.user.customer.model;

import com.needbackend_app.needapp.user.enums.HireStatus;
import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class HireRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(optional = false)
    private NeedUser customer;

    @ManyToOne(optional = false)
    private NeedUser expert;

    private String serviceDescription;

    @Enumerated(EnumType.STRING)
    private HireStatus status = HireStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
}

