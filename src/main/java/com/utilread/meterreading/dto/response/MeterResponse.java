package com.utilread.meterreading.dto.response;

import com.utilread.meterreading.model.MeterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterResponse {

    private UUID id;

    private MeterType meterType;

    private String meterNumber;

    private LocalDate installationDate;

    private String location;

    private Boolean isActive;

    private Integer readingsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
