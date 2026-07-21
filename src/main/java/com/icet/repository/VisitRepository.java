package com.icet.repository;

import com.icet.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByCustomer_CustomerId(Long customerId);

    List<Visit> findByProperty_PropertyId(Long propertyId);

    List<Visit> findByProperty_PropertyIdAndIsVisitedFalse(Long propertyId);

    List<Visit> findByCustomer_CustomerIdAndIsVisitedFalse(Long customerId);
}
