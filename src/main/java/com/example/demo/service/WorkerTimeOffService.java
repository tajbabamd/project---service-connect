package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AddWorkerTimeOffRequest;
import com.example.demo.dto.WorkerTimeOffResponse;

public interface WorkerTimeOffService {

    WorkerTimeOffResponse addTimeOff(
            Long userId,
            AddWorkerTimeOffRequest request);

    List<WorkerTimeOffResponse> getMyTimeOff(Long userId);

    String deleteTimeOff(Long userId, Long timeOffId);
}