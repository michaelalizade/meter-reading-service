package com.utilread.meterreading.repository;

import com.utilread.meterreading.model.Meter;
import com.utilread.meterreading.model.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeterRepository extends JpaRepository<Meter, UUID> {

    List<Meter> findByUserId(UUID userId);

    List<Meter> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    List<Meter> findByUserIdAndMeterType(UUID userId, MeterType meterType);

    Optional<Meter> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndMeterNumber(UUID userId, String meterNumber);

    @Query("SELECT m FROM Meter m WHERE m.user.id = :userId AND m.id = :meterId")
    Optional<Meter> findByIdAndUserIdWithValidation(@Param("meterId") UUID meterId, @Param("userId") UUID userId);

}
