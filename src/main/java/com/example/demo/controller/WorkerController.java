package com.example.demo.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.WorkerRegistrationRequest;
import com.example.demo.service.WorkerService;

import java.util.NoSuchElementException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.demo.dto.UpdateWorkerAvailabilityRequest;
import com.example.demo.entity.User;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerWorker(
            @RequestBody WorkerRegistrationRequest request) {

        try {

            String message = workerService.registerWorker(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(message);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
    @PutMapping("/availability-status")
    public ResponseEntity<String> updateAvailabilityStatus(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateWorkerAvailabilityRequest request) {

        try {

            String message = workerService.updateAvailabilityStatus(
                    user.getUserId(),
                    request.getAvailabilityStatus());

            return ResponseEntity.ok(message);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (NoSuchElementException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }
}