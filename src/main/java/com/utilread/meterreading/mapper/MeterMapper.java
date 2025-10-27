package com.utilread.meterreading.mapper;

import com.utilread.meterreading.dto.request.CreateMeterRequest;
import com.utilread.meterreading.dto.response.MeterResponse;
import com.utilread.meterreading.model.Meter;
import com.utilread.meterreading.model.User;
import org.springframework.stereotype.Component;

@Component
public class MeterMapper {

    public Meter toEntity(CreateMeterRequest request, User user){
        return Meter.builder()
                .user(user)
                .meterType(request.getMeterType())
                .meterNumber(request.getMeterNumber())
                .installationDate(request.getInstallationDate())
                .location(request.getLocation())
                .isActive(true)
                .build();
    }

    public MeterResponse toResponse(Meter meter){
        return MeterResponse.builder()
                .id(meter.getId())
                .meterType(meter.getMeterType())
                .meterNumber(meter.getMeterNumber())
                .installationDate(meter.getInstallationDate())
                .location(meter.getLocation())
                .isActive(meter.getIsActive())
                .readingsCount(meter.getReadings() != null ? meter.getReadings().size() : 0)
                .createdAt(meter.getCreatedAt())
                .updatedAt(meter.getUpdatedAt())
                .build();
    }

    // TODO: research mapstruct

}
