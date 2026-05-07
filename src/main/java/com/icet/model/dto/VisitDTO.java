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
public class VisitDTO {
    private Long visitId;
    private Long customerId;
    private Long propertyId;
    private LocalDate visitPlannedDate;
    private Boolean isVisited;
}

