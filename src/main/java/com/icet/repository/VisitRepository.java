package com.icet.repository;

import com.icet.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByCustomerId(Long customerId);

    List<Visit> findByPropertyId(Long propertyId);

    List<Visit> findByPropertyIdAndIsVisitedFalse(Long propertyId);

    List<Visit> findByCustomerIdAndIsVisitedFalse(Long customerId);
}

