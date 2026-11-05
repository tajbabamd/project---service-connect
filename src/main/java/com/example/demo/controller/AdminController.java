package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PendingWorkerResponse;
import com.example.demo.dto.RejectWorkerRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;

@RestController
@RequestMapping("/api/admin/workers")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // View pending worker registrations
    @GetMapping("/pending")
    public List<PendingWorkerResponse> getPendingWorkers() {

        return adminService.getPendingWorkers();
    }

    // Approve a pending worker
    @PutMapping("/{workerId}/approve")
    public ResponseEntity<String> approveWorker(
            @PathVariable Long workerId,
            @AuthenticationPrincipal User adminUser) {

        try {
            String message = adminService.approveWorker(
                    workerId,
                    adminUser.getUserId()
            );

            return ResponseEntity.ok(message);

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

    // Reject a pending worker
    @PutMapping("/{workerId}/reject")
    public ResponseEntity<String> rejectWorker(
            @PathVariable Long workerId,
            @AuthenticationPrincipal User adminUser,
            @RequestBody RejectWorkerRequest request) {

        try {
            String message = adminService.rejectWorker(
                    workerId,
                    adminUser.getUserId(),
                    request.getReason()
            );

            return ResponseEntity.ok(message);

        } catch (NoSuchElementException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }
}