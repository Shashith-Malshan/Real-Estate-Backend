package com.icet.repository;

import com.icet.model.entity.PropertyDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyDealRepository extends JpaRepository<PropertyDeal, Long> {
    List<PropertyDeal> findByCustomer_CustomerId(Long customerId);

    List<PropertyDeal> findByProperty_PropertyId(Long propertyId);

    boolean existsByProperty_PropertyIdAndCustomer_CustomerId(Long propertyId, Long customerId);
}
