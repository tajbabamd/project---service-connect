package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WorkerServiceArea;

public interface WorkerServiceAreaRepository
        extends JpaRepository<WorkerServiceArea, Long> {

    // Find all service areas registered by a worker
    List<WorkerServiceArea> findByWorker_WorkerId(Long workerId);

    // Find workers serving a particular PIN code
    List<WorkerServiceArea> findByPincode(String pincode);

    // Check if a worker already serves a PIN code
    boolean existsByWorker_WorkerIdAndPincode(
            Long workerId, String pincode);

    // Find a specific service area belonging to a worker
    Optional<WorkerServiceArea>
        findByServiceAreaIdAndWorker_WorkerId(
            Long serviceAreaId, Long workerId);
}