package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WorkerSearchResult;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerSearchService;

@RestController
@RequestMapping("/api/customers/workers")
public class WorkerSearchController {

    private final WorkerSearchService searchService;

    public WorkerSearchController(WorkerSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<WorkerSearchResult> search(
            @AuthenticationPrincipal User user,
            @RequestParam("addressId") Long addressId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime end) {

        return searchService.search(user.getUserId(), addressId, categoryId, start, end);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
