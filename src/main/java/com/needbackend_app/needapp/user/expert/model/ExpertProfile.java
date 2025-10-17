package com.needbackend_app.needapp.user.expert.model;

import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.enums.ApprovalStatus;
import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "expert_profiles")
public class ExpertProfile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String category;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "expert_subcategories",
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "subcategory_id")
    )
    private Set<SubCategory> subCategories = new HashSet<>();

    private Integer yearsOfExperience;
    private String portfolioUrl;
    private String nationalId;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @OneToOne
    @MapsId
    @JoinColumn(name = "needUser_id")
    private NeedUser needUser;
}