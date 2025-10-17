package com.needbackend_app.needapp.user.model;

import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.administrator.admin.model.AdminProfile;
import com.needbackend_app.needapp.user.customer.model.CustomerProfile;
import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "need_user")
public class NeedUser extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean isVerified = false;

    /*For Experts/Admins if assigned to a State*/
    private String state;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State region;

    @OneToOne(mappedBy = "needUser", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToOne(mappedBy = "needUser", cascade = CascadeType.ALL)
    private CustomerProfile customerProfile;

    @OneToOne(mappedBy = "needUser", cascade = CascadeType.ALL)
    private ExpertProfile expertProfile;

    @OneToOne(mappedBy = "needUser", cascade = CascadeType.ALL)
    private AdminProfile adminProfile;

    public String getRoleAsAuthority() {
        return "ROLE_" + this.role.name();
    }
}