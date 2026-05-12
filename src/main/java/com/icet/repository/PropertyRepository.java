package com.icet.repository;

import com.icet.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByPropertyCategory_PropertyCategoryId(Long propertyCategoryId);

    List<Property> findByDistrictContaining(String district);

    List<Property> findByLocationContaining(String location);

    List<Property> findByTitleContaining(String title);

    List<Property> findByDistrictAndPropertyCategory_PropertyCategoryId(String district, Long propertyCategoryId);

    List<Property> findBySeller_SellerId(Long sellerId);
}
