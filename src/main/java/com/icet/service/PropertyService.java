package com.icet.service;

import com.icet.model.dto.PropertyCreateDTO;
import com.icet.model.dto.PropertyDTO;

import java.util.List;

public interface PropertyService {

    List<PropertyDTO> getAllProperties();

    List<PropertyDTO> getLatestProperties(Integer limit, Long categoryId);

    PropertyDTO getPropertyById(Long propertyId);

    List<PropertyDTO> searchByDistrict(String district);

    List<PropertyDTO> searchByLocation(String location);

    List<PropertyDTO> searchByCategory(Long categoryId);

    List<PropertyDTO> searchProperties(String district, Long categoryId);

    List<PropertyDTO> getPropertiesBySeller(Long sellerId);

    PropertyDTO createProperty(PropertyCreateDTO dto);

    PropertyDTO updateProperty(Long propertyId, PropertyDTO dto);

    void deleteProperty(Long propertyId);

    void incrementVisitCount(Long propertyId);

    void incrementInquiryCount(Long propertyId);
}

