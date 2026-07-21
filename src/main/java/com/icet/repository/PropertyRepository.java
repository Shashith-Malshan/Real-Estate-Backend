package com.icet.repository;

import com.icet.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Get the latest N properties ordered by propertyId descending
     */
    @Query(value = "SELECT * FROM property ORDER BY property_id DESC LIMIT :limit", nativeQuery = true)
    List<Property> findLatestProperties(@Param("limit") Integer limit);

    /**
     * Get the latest N properties ordered by propertyId descending, filtered by category
     */
    @Query(value = "SELECT * FROM property WHERE property_category_id = :categoryId ORDER BY property_id DESC LIMIT :limit", nativeQuery = true)
    List<Property> findLatestPropertiesByCategory(@Param("limit") Integer limit, @Param("categoryId") Long categoryId);
}
