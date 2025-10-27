package com.utilread.meterreading.repository;

import com.utilread.meterreading.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    List<Reading> findByMeterId(UUID meterId);

    List<Reading> findByMeterIdOrderByReadingDateDesc(UUID meterId);

    List<Reading> findByMeterIdAndReadingDateBetween(UUID meterId, LocalDate startDate, LocalDate endDate);

    Optional<Reading> findByMeterIdAndReadingDate(UUID meterId, LocalDate readingDate);

    boolean existsByMeterIdAndReadingDate(UUID meterId, LocalDate readingDate);

    @Query("SELECT r FROM Reading r WHERE r.meter.id = :meterId ORDER BY r.readingDate DESC LIMIT 1")
    Optional<Reading> findLatestReadingByMeterId(@Param("meterId") UUID meterId);

    @Query("SELECT r FROM Reading r WHERE r.meter.id = :meterId AND r.readingDate < :date ORDER BY r.readingDate DESC LIMIT 1")
    Optional<Reading> findPreviousReading(@Param("meterId") UUID meterId, @Param("date") LocalDate date);

    @Query("SELECT r FROM Reading r WHERE r.meter.id = :meterId AND EXTRACT(YEAR FROM r.readingDate) = :year ORDER BY r.readingDate")
    List<Reading> findReadingsByMeterIdAndYear(@Param("meterId") UUID meterId, @Param("year") int year);

}
