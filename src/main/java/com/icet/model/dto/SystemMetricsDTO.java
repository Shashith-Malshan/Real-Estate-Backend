package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemMetricsDTO {
    private long totalUsers;
    private long totalProperties;
    private long totalDeals;
    private double platformRevenue;
}
