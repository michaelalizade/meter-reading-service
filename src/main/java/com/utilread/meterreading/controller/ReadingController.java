package com.utilread.meterreading.controller;

import com.utilread.meterreading.dto.request.CreateReadingRequest;
import com.utilread.meterreading.dto.request.UpdateReadingRequest;
import com.utilread.meterreading.dto.response.ConsumptionResponse;
import com.utilread.meterreading.dto.response.ReadingResponse;
import com.utilread.meterreading.security.Authenticated;
import com.utilread.meterreading.service.ReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/meters/{meterId}/readings")
@RequiredArgsConstructor
@Authenticated
@Tag(name = "Reading Management", description = "Endpoints for managing meter readings")
public class ReadingController {

    private final ReadingService readingService;

    @Operation(summary = "Create reading", description = "Creates a reading for a selected meter")
    @PostMapping
    public ResponseEntity<ReadingResponse> createReading(
            @PathVariable UUID meterId,
            @Valid @RequestBody CreateReadingRequest request){
        log.info("Getting reading for meter: {}", meterId);
        ReadingResponse response = readingService.createReading(meterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get readings", description = "Returns a selected meter's list of readings")
    @GetMapping
    public ResponseEntity<List<ReadingResponse>> getReadings(
            @PathVariable UUID meterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        log.info("Getting reading for meter: {}, date range: {} to {}", meterId , startDate, endDate);

        List<ReadingResponse> readings;
        if(startDate != null && endDate != null){
            readings = readingService.getReadingsByDateRange(meterId, startDate, endDate);
        }else{
            readings = readingService.getAllReadingsForMeter(meterId);
        }
        return ResponseEntity.ok(readings);
    }

    @Operation(summary = "Get consumption", description = "Returns a selected meter's consumption amount")
    @GetMapping("/consumption")
    public ResponseEntity<ConsumptionResponse> getConsumption(
            @PathVariable UUID meterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        log.info("Calculating consumption for meter: {} from: {} to {}", meterId, startDate, endDate);

        ConsumptionResponse response = readingService.getConsumption(meterId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update reading", description = "Updates a selected reading by its ID")
    @PatchMapping("/{readingId}")
    public ResponseEntity<ReadingResponse> updateReading(
            @PathVariable UUID meterId,
            @PathVariable UUID readingId,
            @Valid @RequestBody UpdateReadingRequest request){
        log.info("Updating reading: {} for meter: {}", readingId, meterId);

        ReadingResponse response = readingService.updateReading(meterId, readingId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete reading", description = "Deletes a selected reading by its ID")
    @DeleteMapping("/{readingId}")
    public ResponseEntity<Void> deleteReading(
            @PathVariable UUID meterId,
            @PathVariable UUID readingId){
        log.info("Deleting reading: {} for meter: {}", readingId, meterId);

        readingService.deleteReading(meterId, readingId);
        return ResponseEntity.noContent().build();
    }

}
