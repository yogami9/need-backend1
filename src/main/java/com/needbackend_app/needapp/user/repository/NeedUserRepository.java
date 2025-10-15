package com.needbackend_app.needapp.user.repository;

import com.needbackend_app.needapp.user.model.NeedUser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NeedUserRepository extends JpaRepository<NeedUser, UUID> {
    boolean existsByEmail(String email);
    Optional<NeedUser> findByEmail(String email);
    Optional<NeedUser> findByPhone(String phone);

    boolean existsByPhone(@NotBlank(message = "Phone number is required") String phone);
}
