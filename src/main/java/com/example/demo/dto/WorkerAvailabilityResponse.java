package com.example.demo.dto;

import java.time.LocalTime;

public class WorkerAvailabilityResponse {

    private Long availabilityId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;

    public WorkerAvailabilityResponse(
            Long availabilityId,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Boolean isActive) {

        this.availabilityId = availabilityId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }
}