package com.example.demo.dto;

import java.time.LocalDateTime;

public class AddWorkerTimeOffRequest {

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String reason;

    public AddWorkerTimeOffRequest() {
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}