package com.icet.repository;

import com.icet.model.entity.ResidentialProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentialPropertyRepository extends JpaRepository<ResidentialProperty, Long> {
    Optional<ResidentialProperty> findByPropertyId(Long propertyId);
}

