package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDealCreateDTO {
    private Long propertyId;
    private Long customerId;
    private BigDecimal agreedTotalAmount;
}

