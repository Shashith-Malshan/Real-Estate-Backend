package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCreateDTO {
    private String title;
    private String description;
    private String location;
    private String district;
    private Long propertyCategoryId;
    
    // For residential
    private BigDecimal price;
    private Integer bedroomCount;
    private Integer bathroomCount;
    private String residentialType; // house or apartment
    private String residentialStatus; // ongoing or completed
    
    // For commercial
    private BigDecimal floorSize;
    private String commercialType; // office, shopping center, restaurant, hotel
    private String commercialStatus;
    
    // For land
    private Integer plotCount;
    private BigDecimal unitPrice;
    
    // Images
    private List<String> imagePaths;

    // Seller association
    private Long sellerId;
}

