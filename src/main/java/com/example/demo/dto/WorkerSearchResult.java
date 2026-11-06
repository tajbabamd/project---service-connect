package com.example.demo.dto;

public record WorkerSearchResult(
        Long workerId,
        String fullName,
        Integer experienceYears,
        String skillDescription) {
}
