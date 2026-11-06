package com.example.demo.repository;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.WorkerTimeOff;

import jakarta.persistence.LockModeType;

public interface WorkerTimeOffRepository extends JpaRepository<WorkerTimeOff, Long> {

	// Get all time-off records belonging to a worker
	List<WorkerTimeOff> findByWorker_WorkerId(Long workerId);

	// Get a specific time-off record belonging to a worker
	Optional<WorkerTimeOff> findByTimeOffIdAndWorker_WorkerId(Long timeOffId, Long workerId);

	// Check whether requested dates/times overlap with time off
	boolean existsByWorker_WorkerIdAndStartDatetimeLessThanAndEndDatetimeGreaterThan(Long workerId,
			LocalDateTime requestedEnd, LocalDateTime requestedStart);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("""
			select t from WorkerTimeOff t
			where t.worker.workerId = :workerId
			  and t.startDatetime < :requestedEnd
			  and t.endDatetime > :requestedStart
			""")
	List<WorkerTimeOff> findOverlappingTimeOffForUpdate(@Param("workerId") Long workerId,
			@Param("requestedStart") LocalDateTime requestedStart, @Param("requestedEnd") LocalDateTime requestedEnd);

}