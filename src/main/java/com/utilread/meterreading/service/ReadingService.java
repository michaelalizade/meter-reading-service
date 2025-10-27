package com.utilread.meterreading.service;

import com.utilread.meterreading.dto.request.CreateReadingRequest;
import com.utilread.meterreading.dto.request.UpdateReadingRequest;
import com.utilread.meterreading.dto.response.ConsumptionResponse;
import com.utilread.meterreading.dto.response.ReadingResponse;
import com.utilread.meterreading.exception.DuplicateResourceException;
import com.utilread.meterreading.exception.ResourceNotFoundException;
import com.utilread.meterreading.mapper.ReadingMapper;
import com.utilread.meterreading.model.Meter;
import com.utilread.meterreading.model.Reading;
import com.utilread.meterreading.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final MeterService meterService;
    private final ReadingMapper readingMapper;

    @Transactional
    public ReadingResponse createReading(UUID meterId, CreateReadingRequest request){
        Meter meter = meterService.findMeterByIdAndValidateOwnership(meterId);

        if(readingRepository.existsByMeterIdAndReadingDate(meterId, request.getReadingDate())){
            throw new DuplicateResourceException("Reading already exists for date: " + request.getReadingDate());
        }

        Reading reading = readingMapper.toEntity(request, meter);
        reading = readingRepository.save(reading);

        log.info("Created reading {} for meter {}", reading.getId(), meterId);
        return readingMapper.toResponse(reading);
    }

    @Transactional
    public ReadingResponse updateReading(UUID meterId, UUID readingId, UpdateReadingRequest request){
        meterService.findMeterByIdAndValidateOwnership(meterId);

        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found with id: " + readingId));

        if(!reading.getMeter().getId().equals(meterId)){
            throw new ResourceNotFoundException("Reading not found for this meter");
        }

        if (request.getReadingValue() != null){
            reading.setReadingValue(request.getReadingValue());
        }
        if (request.getReadingDate() != null){

            if(!reading.getReadingDate().equals(request.getReadingDate()) &&
            readingRepository.existsByMeterIdAndReadingDate(meterId, request.getReadingDate())){
                throw new DuplicateResourceException("Reading already exists for date: " + request.getReadingDate());
            }
            reading.setReadingDate(request.getReadingDate());
        }
        if (request.getNotes() != null){
            reading.setNotes(request.getNotes());
        }
        reading = readingRepository.save(reading);
        log.info("Updated reading: {}", readingId);
        return readingMapper.toResponse(reading);
    }

    @Transactional(readOnly = true)
    public List<ReadingResponse> getAllReadingsForMeter(UUID meterId){
        meterService.findMeterByIdAndValidateOwnership(meterId);

        List<Reading> readings = readingRepository.findByMeterIdOrderByReadingDateDesc(meterId);
        return readings.stream()
                .map(readingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReadingResponse> getReadingsByDateRange(UUID meterId, LocalDate startDate, LocalDate endDate){
        meterService.findMeterByIdAndValidateOwnership(meterId);

        List<Reading> readings = readingRepository.findByMeterIdAndReadingDateBetween(meterId, startDate, endDate);
        return readings.stream()
                .map(readingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsumptionResponse getConsumption(UUID meterId, LocalDate startDate, LocalDate endDate){
        meterService.findMeterByIdAndValidateOwnership(meterId);

        Reading startReading = readingRepository.findByMeterIdAndReadingDate(meterId, startDate)
                .orElseThrow(() -> new ResourceNotFoundException("No reading found for start date: " + startDate));

        Reading endReading = readingRepository.findByMeterIdAndReadingDate(meterId, endDate)
                .orElseThrow(() -> new ResourceNotFoundException("No reading found for end date: " + endDate));

        BigDecimal consumption = endReading.getReadingValue().subtract(startReading.getReadingValue());
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal averagePerDay = days > 0
                ? consumption.divide(BigDecimal.valueOf(days), 3, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ConsumptionResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .startReading(startReading.getReadingValue())
                .endReading(endReading.getReadingValue())
                .consumption(consumption)
                .daysInPeriod((int) days)
                .averagePerDay(averagePerDay)
                .build();
    }

    @Transactional
    public void deleteReading(UUID meterId, UUID readingId){
        meterService.findMeterByIdAndValidateOwnership(meterId);

        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found with id: " + readingId));

        if(!reading.getMeter().getId().equals(meterId)){
            throw new ResourceNotFoundException("Reading not found for this meter");
        }

        readingRepository.delete(reading);
        log.info("Deleted reading {}", reading);
    }

}
