package com.utilread.meterreading.service;

import com.utilread.meterreading.dto.request.CreateMeterRequest;
import com.utilread.meterreading.dto.response.MeterResponse;
import com.utilread.meterreading.exception.DuplicateResourceException;
import com.utilread.meterreading.exception.ResourceNotFoundException;
import com.utilread.meterreading.exception.UnauthorizedAccessException;
import com.utilread.meterreading.mapper.MeterMapper;
import com.utilread.meterreading.model.Meter;
import com.utilread.meterreading.model.MeterType;
import com.utilread.meterreading.model.User;
import com.utilread.meterreading.repository.MeterRepository;
import com.utilread.meterreading.security.SecurityContext;
import com.utilread.meterreading.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeterService {

    private final MeterRepository meterRepository;
    private final UserService userService;
    private final MeterMapper meterMapper;

    @Transactional
    public MeterResponse createMeter(CreateMeterRequest request){

        UserPrincipal principal = SecurityContext.getCurrentUser();
        User user = userService.getOrCreateUser(principal);

        if(meterRepository.existsByUserIdAndMeterNumber(user.getId(), request.getMeterNumber())){
            throw new DuplicateResourceException("Meter with number '" + request.getMeterNumber() + "' already exists");
        }

        Meter meter = meterMapper.toEntity(request, user);

        log.info("Saved meter with ID: {}, createdAt: {}", meter.getId(), meter.getCreatedAt());

        meter.setCreatedAt(LocalDateTime.now());
        meter.setUpdatedAt(LocalDateTime.now());

        meter = meterRepository.saveAndFlush(meter);

        log.info("Created meter {} for user {}", meter.getId(), user.getEmail());
        return meterMapper.toResponse(meter);

        // TODO: when pulling entity, research why patching it will auto persist it (without saveAndFlush)
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> getAllMeterForCurrentUser(){
        UserPrincipal principal = SecurityContext.getCurrentUser();
        User user = userService.getOrCreateUser(principal);

        List<Meter> meters = meterRepository.findByUserId(user.getId());
        return meters.stream()
                .map(meterMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeterResponse> getMetersByType(MeterType meterType){
        UserPrincipal principal = SecurityContext.getCurrentUser();
        User user = userService.getOrCreateUser(principal);

        List<Meter> meters = meterRepository.findByUserIdAndMeterType(user.getId(), meterType);
        return meters.stream()
                .map(meterMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeterResponse getMeterById(UUID meterId){
        Meter meter = findMeterByIdAndValidateOwnership(meterId);
        return meterMapper.toResponse(meter);
    }

    @Transactional
    public void deactivateMeter(UUID meterId){
        Meter meter = findMeterByIdAndValidateOwnership(meterId);
        meter.setIsActive(false);
        meterRepository.save(meter);
        log.info("Deactivated meter: {}", meterId);
    }

    public Meter findMeterByIdAndValidateOwnership(UUID meterId){
        UserPrincipal principal = SecurityContext.getCurrentUser();
        User user = userService.getOrCreateUser(principal);

        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + meterId));
        if (!meter.getUser().getId().equals(user.getId())){
            throw new UnauthorizedAccessException("You don't have access to this meter");
        }

        return meter;
    }

}
