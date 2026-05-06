package com.icet.repository;

import com.icet.model.entity.SellerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerTypeRepository extends JpaRepository<SellerType, Long> {
    Optional<SellerType> findByType(String type);
}

