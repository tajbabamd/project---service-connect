package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AddWorkerServiceAreaRequest;
import com.example.demo.dto.WorkerServiceAreaResponse;

import com.example.demo.entity.User;

import com.example.demo.service.WorkerServiceAreaService;

@RestController
@RequestMapping("/api/workers/service-areas")
public class WorkerServiceAreaController {

    private final WorkerServiceAreaService serviceAreaService;

    public WorkerServiceAreaController(
            WorkerServiceAreaService serviceAreaService) {

        this.serviceAreaService = serviceAreaService;
    }

    // Add a service area

    @PostMapping
    public ResponseEntity<WorkerServiceAreaResponse> addServiceArea(
            @AuthenticationPrincipal User user,
            @RequestBody AddWorkerServiceAreaRequest request) {

        WorkerServiceAreaResponse response =
                serviceAreaService.addServiceArea(
                        user.getUserId(),
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // View own service areas

    @GetMapping
    public List<WorkerServiceAreaResponse> getMyServiceAreas(
            @AuthenticationPrincipal User user) {

        return serviceAreaService
                .getMyServiceAreas(user.getUserId());
    }

    // Delete own service area

    @DeleteMapping("/{areaId}")
    public ResponseEntity<String> removeServiceArea(
            @AuthenticationPrincipal User user,
            @PathVariable Long areaId) {

        String message = serviceAreaService.removeServiceArea(
                user.getUserId(),
                areaId);

        return ResponseEntity.ok(message);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(
            NoSuchElementException e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(
            IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
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