package com.example.demo.dto;

import java.time.LocalDateTime;

public class WorkerTimeOffResponse {

    private Long timeOffId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String reason;

    public WorkerTimeOffResponse(
            Long timeOffId,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            String reason) {

        this.timeOffId = timeOffId;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.reason = reason;
    }

    public Long getTimeOffId() {
        return timeOffId;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public String getReason() {
        return reason;
    }
}