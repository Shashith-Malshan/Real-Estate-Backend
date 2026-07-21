package com.icet.service.mapper;

import com.icet.model.dto.PropertyDTO;
import com.icet.model.entity.CommercialProperty;
import com.icet.model.entity.Land;
import com.icet.model.entity.Property;
import com.icet.model.entity.PropertyCategory;
import com.icet.model.entity.PropertyImage;
import com.icet.model.entity.ResidentialProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    public PropertyDTO toPropertyDTO(Property property, PropertyCategory category,
                                     ResidentialProperty residential,
                                     CommercialProperty commercial,
                                     Land land, List<PropertyImage> images) {
        PropertyDTO dto = new PropertyDTO();
        dto.setPropertyId(property.getPropertyId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setLocation(property.getLocation());
        dto.setDistrict(property.getDistrict());
        dto.setPropertyCategoryId(property.getPropertyCategory().getPropertyCategoryId());

        if (category != null) {
            dto.setCategoryName(category.getName());
        }

        dto.setVisitCount(property.getVisitCount());
        dto.setInquiryCount(property.getInquiryCount());

        // Populate sellerId if seller is associated
        if (property.getSeller() != null) {
            dto.setSellerId(property.getSeller().getSellerId());
        }

        // Map images to imageUrls (base64 strings)
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .map(PropertyImage::getImagePath)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        }

        // Add type-specific details
        if (residential != null) {
            dto.setPrice(residential.getPrice());
            dto.setType(residential.getType());
            dto.setStatus(residential.getStatus());
            dto.setBedroomCount(residential.getBedroomCount());
            dto.setBathroomCount(residential.getBathroomCount());
        } else if (commercial != null) {
            dto.setPrice(commercial.getPrice());
            dto.setType(commercial.getType());
            dto.setStatus(commercial.getStatus());
            dto.setFloorSize(commercial.getFloorSize());
        } else if (land != null) {
            dto.setUnitPrice(land.getUnitPrice());
            dto.setPlotCount(land.getPlotCount());
            dto.setType("Land");
        }

        return dto;
    }

    public Property toPropertyEntity(PropertyDTO dto, PropertyCategory category) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setLocation(dto.getLocation());
        property.setDistrict(dto.getDistrict());
        property.setPropertyCategory(category);
        property.setVisitCount(0);
        property.setInquiryCount(0);
        return property;
    }
}
