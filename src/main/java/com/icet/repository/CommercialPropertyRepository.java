package com.icet.repository;

import com.icet.model.entity.CommercialProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommercialPropertyRepository extends JpaRepository<CommercialProperty, Long> {
    Optional<CommercialProperty> findByProperty_PropertyId(Long propertyId);
}
