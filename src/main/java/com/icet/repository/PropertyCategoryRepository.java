package com.icet.repository;

import com.icet.model.entity.PropertyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long> {
    Optional<PropertyCategory> findByName(String name);
}

