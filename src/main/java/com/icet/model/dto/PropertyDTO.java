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
public class PropertyDTO {
    private Long propertyId;
    private String title;
    private String description;
    private String location;
    private String district;
    private Long propertyCategoryId;
    private String categoryName;
    private Integer visitCount;
    private Integer inquiryCount;
    private BigDecimal price;
    private String type;
    private String status;
    private Integer bedroomCount;
    private Integer bathroomCount;
    private BigDecimal floorSize;
    private Integer plotCount;
    private BigDecimal unitPrice;
    private Long sellerId;
    private List<String> imageUrls;
}

