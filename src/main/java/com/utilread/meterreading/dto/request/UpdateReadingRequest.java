package com.utilread.meterreading.dto.request;

import jakarta.validation.constraints.Positive;
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
public class UpdateReadingRequest {

    @Positive(message = "Reading value must be positive")
    private BigDecimal readingValue;

    private LocalDate readingDate;

    private String notes;

}
