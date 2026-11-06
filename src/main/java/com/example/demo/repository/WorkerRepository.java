package com.example.demo.repository;

import java.util.List;

import java.util.Optional;


import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Worker;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AvailabilityStatus;
import com.example.demo.entity.VerificationStatus;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

	Optional<Worker> findByUser_UserId(Long userId);

	List<Worker> findByVerificationStatus(VerificationStatus verificationStatus);

	List<Worker> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);

	List<Worker> findByCityAndVerificationStatusAndAvailabilityStatus(String city,
			VerificationStatus verificationStatus, AvailabilityStatus availabilityStatus);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select w from Worker w where w.workerId = :workerId")
	Optional<Worker> findByIdForUpdate(@Param("workerId") Long workerId);

}