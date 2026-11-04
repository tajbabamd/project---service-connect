package com.example.demo.service;

import com.example.demo.dto.WorkerRegistrationRequest;

public interface WorkerService {

    String registerWorker(WorkerRegistrationRequest request);
}