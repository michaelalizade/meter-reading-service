package com.utilread.meterreading.dto.request;

import com.utilread.meterreading.model.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeterRequest {

    @NotNull(message = "Meter type is required")
    private MeterType meterType;

    @NotBlank(message = "Meter number is required")
    private String meterNumber;

    private LocalDate installationDate;

    private String location;

}
