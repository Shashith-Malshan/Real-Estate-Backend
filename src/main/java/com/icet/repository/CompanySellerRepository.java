package com.icet.repository;

import com.icet.model.entity.CompanySeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanySellerRepository extends JpaRepository<CompanySeller, Long> {
    Optional<CompanySeller> findBySeller_SellerId(Long sellerId);
}
