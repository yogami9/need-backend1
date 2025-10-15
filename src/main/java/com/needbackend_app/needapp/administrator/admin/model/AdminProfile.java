package com.needbackend_app.needapp.administrator.admin.model;

import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "admin_profiles")
public class AdminProfile {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    private String assignedState;
    private String notes;

    @OneToOne
    @MapsId
    @JoinColumn(name = "needUser_id")
    private NeedUser needUser;
}