package com.utilread.meterreading.mapper;

import com.utilread.meterreading.dto.request.CreateReadingRequest;
import com.utilread.meterreading.dto.response.ReadingResponse;
import com.utilread.meterreading.model.Meter;
import com.utilread.meterreading.model.Reading;
import org.springframework.stereotype.Component;

@Component
public class ReadingMapper {

    public Reading toEntity(CreateReadingRequest request, Meter meter){
        return Reading.builder()
                .meter(meter)
                .readingValue(request.getReadingValue())
                .readingDate(request.getReadingDate())
                .notes(request.getNotes())
                .build();
    }

    public ReadingResponse toResponse(Reading reading){
        return ReadingResponse.builder()
                .id(reading.getId())
                .meterId(reading.getMeter().getId())
                .readingValue(reading.getReadingValue())
                .readingDate(reading.getReadingDate())
                .notes(reading.getNotes())
                .createdAt(reading.getCreatedAt())
                .updatedAt(reading.getUpdatedAt())
                .build();
    }

}
