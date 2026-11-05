package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddWorkerAvailabilityRequest;
import com.example.demo.dto.WorkerAvailabilityResponse;

import com.example.demo.entity.Worker;
import com.example.demo.entity.WorkerAvailability;

import com.example.demo.repository.WorkerRepository;
import com.example.demo.repository.WorkerAvailabilityRepository;

import com.example.demo.service.WorkerAvailabilityService;

@Service
public class WorkerAvailabilityServiceImpl
        implements WorkerAvailabilityService {

    private final WorkerRepository workerRepository;
    private final WorkerAvailabilityRepository availabilityRepository;

    public WorkerAvailabilityServiceImpl(
            WorkerRepository workerRepository,
            WorkerAvailabilityRepository availabilityRepository) {

        this.workerRepository = workerRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    @Transactional
    public WorkerAvailabilityResponse addAvailability(
            Long userId,
            AddWorkerAvailabilityRequest request) {

        // Step 1: Validate request
        if (request == null
                || request.getDayOfWeek() == null
                || request.getStartTime() == null
                || request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Day, start time and end time are required");
        }

        int day = request.getDayOfWeek();

        if (day < 1 || day > 7) {
            throw new IllegalArgumentException(
                    "Day of week must be between 1 and 7");
        }

        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        // Step 2: Find the authenticated worker
        Worker worker = getWorker(userId);

        // Step 3: Find existing active working periods
        List<WorkerAvailability> existingSlots =
                availabilityRepository
                    .findByWorker_WorkerIdAndDayOfWeekAndIsActiveTrue(
                            worker.getWorkerId(),
                            day);

        // Step 4: Prevent overlapping working periods
        for (WorkerAvailability existing : existingSlots) {

            boolean overlaps =
                    request.getStartTime().isBefore(
                            existing.getEndTime())
                    &&
                    request.getEndTime().isAfter(
                            existing.getStartTime());

            if (overlaps) {
                throw new IllegalStateException(
                        "Working hours overlap with an existing schedule");
            }
        }

        // Step 5: Create and save the schedule
        WorkerAvailability availability =
                new WorkerAvailability();

        availability.setWorker(worker);
        availability.setDayOfWeek(day);
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());

        // The entity already defaults isActive to true.

        WorkerAvailability saved =
                availabilityRepository.save(availability);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerAvailabilityResponse> getMyAvailability(
            Long userId) {

        Worker worker = getWorker(userId);

        List<WorkerAvailability> schedules =
                availabilityRepository.findByWorker_WorkerId(
                        worker.getWorkerId());

        List<WorkerAvailabilityResponse> response =
                new ArrayList<>();

        for (WorkerAvailability schedule : schedules) {
            response.add(toResponse(schedule));
        }

        return response;
    }

    @Override
    @Transactional
    public String removeAvailability(
            Long userId,
            Long availabilityId) {

        Worker worker = getWorker(userId);

        WorkerAvailability availability =
                availabilityRepository.findById(availabilityId)
                    .orElseThrow(() ->
                        new NoSuchElementException(
                                "Availability schedule not found"));

        // A worker cannot delete another worker's schedule.
        if (!availability.getWorker().getWorkerId()
                .equals(worker.getWorkerId())) {

            throw new NoSuchElementException(
                    "Availability schedule not found");
        }

        availabilityRepository.delete(availability);

        return "Availability schedule removed successfully";
    }

    private Worker getWorker(Long userId) {

        return workerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                    new NoSuchElementException(
                            "Worker profile not found"));
    }

    private WorkerAvailabilityResponse toResponse(
            WorkerAvailability availability) {

        return new WorkerAvailabilityResponse(
                availability.getAvailabilityId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getIsActive()
        );
    }
}