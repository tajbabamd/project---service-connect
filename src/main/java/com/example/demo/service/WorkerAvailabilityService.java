package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AddWorkerAvailabilityRequest;
import com.example.demo.dto.WorkerAvailabilityResponse;

public interface WorkerAvailabilityService {

    WorkerAvailabilityResponse addAvailability(
            Long userId,
            AddWorkerAvailabilityRequest request);

    List<WorkerAvailabilityResponse> getMyAvailability(
            Long userId);

    String removeAvailability(
            Long userId,
            Long availabilityId);
}