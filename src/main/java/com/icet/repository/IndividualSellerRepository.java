package com.icet.repository;

import com.icet.model.entity.IndividualSeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndividualSellerRepository extends JpaRepository<IndividualSeller, Long> {
    Optional<IndividualSeller> findBySeller_SellerId(Long sellerId);
}
