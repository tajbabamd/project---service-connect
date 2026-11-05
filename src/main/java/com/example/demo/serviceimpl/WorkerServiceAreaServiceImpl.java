package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddWorkerServiceAreaRequest;
import com.example.demo.dto.WorkerServiceAreaResponse;

import com.example.demo.entity.Worker;
import com.example.demo.entity.WorkerServiceArea;

import com.example.demo.repository.WorkerRepository;
import com.example.demo.repository.WorkerServiceAreaRepository;

import com.example.demo.service.WorkerServiceAreaService;

@Service
public class WorkerServiceAreaServiceImpl
        implements WorkerServiceAreaService {

    private final WorkerRepository workerRepository;

    private final WorkerServiceAreaRepository
            workerServiceAreaRepository;

    public WorkerServiceAreaServiceImpl(
            WorkerRepository workerRepository,
            WorkerServiceAreaRepository workerServiceAreaRepository) {

        this.workerRepository = workerRepository;
        this.workerServiceAreaRepository =
                workerServiceAreaRepository;
    }

    @Override
    @Transactional
    public WorkerServiceAreaResponse addServiceArea(
            Long userId,
            AddWorkerServiceAreaRequest request) {

        // Step 1: Validate request

        if (request == null
                || request.getCity() == null
                || request.getCity().isBlank()
                || request.getState() == null
                || request.getState().isBlank()
                || request.getPincode() == null
                || !request.getPincode().matches("\\d{6}")) {

            throw new IllegalArgumentException(
                    "Valid city, state and 6-digit PIN code are required");
        }

        String city = request.getCity().trim();
        String state = request.getState().trim();
        String pincode = request.getPincode();

        if (city.length() > 100 || state.length() > 100) {
            throw new IllegalArgumentException(
                    "City and state must not exceed 100 characters");
        }

        // Step 2: Find authenticated worker

        Worker worker = getWorker(userId);

        // Step 3: Check whether PIN code already exists

        boolean exists = workerServiceAreaRepository
                .existsByWorker_WorkerIdAndPincode(
                        worker.getWorkerId(),
                        pincode);

        if (exists) {
            throw new IllegalStateException(
                    "Service area already registered");
        }

        // Step 4: Create service area

        WorkerServiceArea area = new WorkerServiceArea();

        area.setWorker(worker);
        area.setCity(city);
        area.setState(state);
        area.setPincode(pincode);

        // Step 5: Save to MySQL

        WorkerServiceArea savedArea =
                workerServiceAreaRepository.save(area);

        return toResponse(savedArea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerServiceAreaResponse> getMyServiceAreas(
            Long userId) {

        Worker worker = getWorker(userId);

        List<WorkerServiceArea> areas =
                workerServiceAreaRepository
                        .findByWorker_WorkerId(
                                worker.getWorkerId());

        List<WorkerServiceAreaResponse> response =
                new ArrayList<>();

        for (WorkerServiceArea area : areas) {
            response.add(toResponse(area));
        }

        return response;
    }

    @Override
    @Transactional
    public String removeServiceArea(
            Long userId,
            Long areaId) {

        Worker worker = getWorker(userId);

        WorkerServiceArea area = workerServiceAreaRepository
                .findByServiceAreaIdAndWorker_WorkerId(
                        areaId,
                        worker.getWorkerId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Service area not found"));

        workerServiceAreaRepository.delete(area);

        return "Service area removed successfully";
    }

    // Find worker from the authenticated user ID

    private Worker getWorker(Long userId) {

        return workerRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker profile not found"));
    }

    // Convert entity into response DTO

    private WorkerServiceAreaResponse toResponse(
            WorkerServiceArea area) {

        return new WorkerServiceAreaResponse(
                area.getServiceAreaId(),
                area.getCity(),
                area.getState(),
                area.getPincode()
        );
    }
}