package com.example.demo.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkerAvailability;

public interface WorkerAvailabilityRepository
        extends JpaRepository<WorkerAvailability, Long> {

    // Find all availability records for a worker
    List<WorkerAvailability> findByWorker_WorkerId(Long workerId);

    // Find a worker's schedule for a specific day
    List<WorkerAvailability> findByWorker_WorkerIdAndDayOfWeek(
            Long workerId,
            Integer dayOfWeek
    );

    // Find active schedules for a worker on a specific day
    List<WorkerAvailability>
        findByWorker_WorkerIdAndDayOfWeekAndIsActiveTrue(
            Long workerId,
            Integer dayOfWeek
        );

    // Find schedules covering a requested time period
    List<WorkerAvailability>
        findByWorker_WorkerIdAndDayOfWeekAndIsActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long workerId,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
        );
}