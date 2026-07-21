package com.icet.repository;

import com.icet.model.entity.Land;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandRepository extends JpaRepository<Land, Long> {
    Optional<Land> findByProperty_PropertyId(Long propertyId);
}
