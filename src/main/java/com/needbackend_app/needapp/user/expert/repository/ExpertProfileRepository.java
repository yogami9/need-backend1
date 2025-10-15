package com.needbackend_app.needapp.user.expert.repository;

import com.needbackend_app.needapp.user.expert.model.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, UUID> {

    @Query("SELECT e FROM ExpertProfile e JOIN e.subCategories s WHERE s.id = :subcategoryId")
    List<ExpertProfile> findAllBySubcategoryId(UUID subcategoryId);

    Optional<ExpertProfile> findByNeedUser_Phone(String phone);

}
