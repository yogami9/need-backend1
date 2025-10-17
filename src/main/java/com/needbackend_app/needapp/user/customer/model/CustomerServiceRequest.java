package com.needbackend_app.needapp.user.customer.model;

import com.needbackend_app.needapp.user.enums.RequestStatus;
import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class CustomerServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private NeedUser customer;

    private String description;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime createdAt;

    public CustomerServiceRequest (Category category, SubCategory subCategory, String description) {
        this.id = UUID.randomUUID();
        this.category = category;
        this.subCategory = subCategory;
        this.description = description;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
