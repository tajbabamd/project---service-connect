package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AddWorkerSkillRequest;
import com.example.demo.dto.WorkerSkillResponse;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerSkillService;

@RestController
@RequestMapping("/api/workers/skills")
public class WorkerSkillController {

    private final WorkerSkillService workerSkillService;

    public WorkerSkillController(WorkerSkillService workerSkillService) {
        this.workerSkillService = workerSkillService;
    }

    @PostMapping
    public ResponseEntity<WorkerSkillResponse> addSkill(
            @AuthenticationPrincipal User user,
            @RequestBody AddWorkerSkillRequest request) {

        WorkerSkillResponse response =
                workerSkillService.addSkill(
                        user.getUserId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<WorkerSkillResponse> getMySkills(
            @AuthenticationPrincipal User user) {

        return workerSkillService.getMySkills(user.getUserId());
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<String> removeSkill(
            @AuthenticationPrincipal User user,
            @PathVariable Long skillId) {

        String message = workerSkillService.removeSkill(
                user.getUserId(), skillId);

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