package com.example.demo.dto;

import com.example.demo.entity.AvailabilityStatus;

public class UpdateWorkerAvailabilityRequest {

    private AvailabilityStatus availabilityStatus;

    public UpdateWorkerAvailabilityRequest() {
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(
            AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}