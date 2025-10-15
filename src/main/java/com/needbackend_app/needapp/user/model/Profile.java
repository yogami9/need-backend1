package com.needbackend_app.needapp.user.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_profiles")
public class Profile {

    @Id
    private UUID id;

    private String bio;
    private String avatarUrl;
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "needUser_id")
    private NeedUser needUser;
}
