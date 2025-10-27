package com.utilread.meterreading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionResponse {
    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal startReading;

    private BigDecimal endReading;

    private BigDecimal consumption;

    private Integer daysInPeriod;

    private BigDecimal averagePerDay;
}