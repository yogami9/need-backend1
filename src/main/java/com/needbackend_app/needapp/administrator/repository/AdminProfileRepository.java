package com.needbackend_app.needapp.administrator.repository;

import com.needbackend_app.needapp.administrator.admin.model.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, UUID> {}


