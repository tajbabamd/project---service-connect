package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AddWorkerAvailabilityRequest;
import com.example.demo.dto.WorkerAvailabilityResponse;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerAvailabilityService;

@RestController
@RequestMapping("/api/workers/availability")
public class WorkerAvailabilityController {

    private final WorkerAvailabilityService availabilityService;

    public WorkerAvailabilityController(
            WorkerAvailabilityService availabilityService) {

        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<WorkerAvailabilityResponse> addAvailability(
            @AuthenticationPrincipal User user,
            @RequestBody AddWorkerAvailabilityRequest request) {

        WorkerAvailabilityResponse response =
                availabilityService.addAvailability(
                        user.getUserId(),
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<WorkerAvailabilityResponse> getMyAvailability(
            @AuthenticationPrincipal User user) {

        return availabilityService.getMyAvailability(
                user.getUserId());
    }

    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<String> removeAvailability(
            @AuthenticationPrincipal User user,
            @PathVariable Long availabilityId) {

        String message = availabilityService.removeAvailability(
                user.getUserId(),
                availabilityId);

        return ResponseEntity.ok(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(
            IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(
            NoSuchElementException e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> conflict(
            IllegalStateException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}