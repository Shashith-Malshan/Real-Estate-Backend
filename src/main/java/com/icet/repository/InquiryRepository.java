package com.icet.repository;

import com.icet.model.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByCustomer_CustomerId(Long customerId);

    List<Inquiry> findByProperty_PropertyId(Long propertyId);

    List<Inquiry> findByProperty_PropertyIdAndIsRepliedFalse(Long propertyId);

    List<Inquiry> findByCustomer_CustomerIdAndIsRepliedFalse(Long customerId);
}
