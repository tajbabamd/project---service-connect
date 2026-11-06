package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.WorkerSearchResult;

public interface WorkerSearchService {

    List<WorkerSearchResult> search(Long userId, Long addressId,
            Long categoryId, LocalDateTime start, LocalDateTime end);
}
