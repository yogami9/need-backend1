package com.needbackend_app.needapp.user.customer.repository;

import com.needbackend_app.needapp.user.customer.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    List<SubCategory> findByCategoryId(UUID categoryId);
    List<SubCategory> findByNameInIgnoreCase(List<String> names);

}
