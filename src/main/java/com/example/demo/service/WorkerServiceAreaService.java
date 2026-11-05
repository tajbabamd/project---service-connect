package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AddWorkerServiceAreaRequest;
import com.example.demo.dto.WorkerServiceAreaResponse;

public interface WorkerServiceAreaService {

    WorkerServiceAreaResponse addServiceArea(
            Long userId,
            AddWorkerServiceAreaRequest request);

    List<WorkerServiceAreaResponse> getMyServiceAreas(
            Long userId);

    String removeServiceArea(
            Long userId,
            Long areaId);
}