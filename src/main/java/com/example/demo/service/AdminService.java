package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.PendingWorkerResponse;

public interface AdminService {

    List<PendingWorkerResponse> getPendingWorkers();

    String approveWorker(Long workerId, Long adminUserId);

    String rejectWorker(
            Long workerId,
            Long adminUserId,
            String reason);
}