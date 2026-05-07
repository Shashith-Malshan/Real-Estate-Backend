package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {
    private Long inquiryId;
    private Long customerId;
    private Long propertyId;
    private String message;
    private LocalDate inquiryDate;
    private Boolean isReplied;
}

