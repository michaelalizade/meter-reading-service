package com.utilread.meterreading.controller;

import com.utilread.meterreading.dto.request.CreateMeterRequest;
import com.utilread.meterreading.dto.response.MeterResponse;
import com.utilread.meterreading.model.MeterType;
import com.utilread.meterreading.security.Authenticated;
import com.utilread.meterreading.service.MeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/meters")
@RequiredArgsConstructor
@Authenticated
@Tag(name = "Meter Management", description = "Endpoints for managing user meters")
public class MeterController {

    private final MeterService meterService;

    @Operation(summary = "Create new meter", description = "Registers a new meter for the current user")
    @PostMapping
    public ResponseEntity<MeterResponse> createMeter(@Valid @RequestBody CreateMeterRequest request){
        log.info("Creating meter of type: {}", request.getMeterType());
        MeterResponse response = meterService.createMeter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all meters", description = "Retrieves all meters for the current user")
    @GetMapping
    public ResponseEntity<List<MeterResponse>> getAllMeters(@RequestParam(required = false)MeterType type){
        log.info("Getting all meters, type filter: {}", type);

        List<MeterResponse> meters = type != null
                ? meterService.getMetersByType(type)
                : meterService.getAllMeterForCurrentUser();

        return ResponseEntity.ok(meters);
    }

    @Operation(summary = "Get meter by meterId", description = "Returns a meter by its ID")
    @GetMapping("/{meterId}")
    public ResponseEntity<MeterResponse> getMeterById(@PathVariable UUID meterId){
        log.info("Getting meter: {}", meterId);
        MeterResponse response = meterService.getMeterById(meterId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete meter by meterId", description = "Deletes a meter by its ID")
    @DeleteMapping("/{meterId}")
    public ResponseEntity<MeterResponse> deactivateMeter(@PathVariable UUID meterId){
        log.info("Deactivating meter: {}", meterId);
        meterService.deactivateMeter(meterId);
        return ResponseEntity.noContent().build();
    }

}
