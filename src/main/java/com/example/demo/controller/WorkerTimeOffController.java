package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AddWorkerTimeOffRequest;
import com.example.demo.dto.WorkerTimeOffResponse;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerTimeOffService;

@RestController
@RequestMapping("/api/workers/time-off")
public class WorkerTimeOffController {

    private final WorkerTimeOffService timeOffService;

    public WorkerTimeOffController(
            WorkerTimeOffService timeOffService) {

        this.timeOffService = timeOffService;
    }

    @PostMapping
    public ResponseEntity<WorkerTimeOffResponse> addTimeOff(
            @AuthenticationPrincipal User user,
            @RequestBody AddWorkerTimeOffRequest request) {

        WorkerTimeOffResponse response =
                timeOffService.addTimeOff(
                        user.getUserId(),
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<WorkerTimeOffResponse> getMyTimeOff(
            @AuthenticationPrincipal User user) {

        return timeOffService.getMyTimeOff(user.getUserId());
    }

    @DeleteMapping("/{timeOffId}")
    public ResponseEntity<String> deleteTimeOff(
            @AuthenticationPrincipal User user,
            @PathVariable Long timeOffId) {

        String message = timeOffService.deleteTimeOff(
                user.getUserId(),
                timeOffId);

        return ResponseEntity.ok(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(
            IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
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