package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDealDTO {
    private Long dealId;
    private Long propertyId;
    private Long customerId;
    private BigDecimal agreedTotalAmount;
    private LocalDate dealDate;
}

