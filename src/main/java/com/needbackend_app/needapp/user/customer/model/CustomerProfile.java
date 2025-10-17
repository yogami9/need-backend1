package com.needbackend_app.needapp.user.customer.model;

import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "customer_profiles")
public class CustomerProfile {

    @Id
    private UUID id;

    private String address;
    private String gender;
    private String avatarUrl;

    @OneToOne
    @MapsId
    @JoinColumn(name = "needUser_id")
    private NeedUser needUser;
}