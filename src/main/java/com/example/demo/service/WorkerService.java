package com.example.demo.service;

import com.example.demo.dto.WorkerRegistrationRequest;
import com.example.demo.entity.AvailabilityStatus;

public interface WorkerService {

    String registerWorker(WorkerRegistrationRequest request);
    
    String updateAvailabilityStatus(
            Long userId,
            AvailabilityStatus status);
}