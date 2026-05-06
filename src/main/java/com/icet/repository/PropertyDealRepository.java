package com.icet.repository;

import com.icet.model.entity.PropertyDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyDealRepository extends JpaRepository<PropertyDeal, Long> {
    List<PropertyDeal> findByCustomerId(Long customerId);

    List<PropertyDeal> findByPropertyId(Long propertyId);

    boolean existsByPropertyIdAndCustomerId(Long propertyId, Long customerId);
}

