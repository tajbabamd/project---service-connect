
package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponse {

    private Long bookingId;
    private Long addressId;
    private Long categoryId;
    private Long workerId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String bookingStatus;

    public BookingResponse(
            Long bookingId,
            Long addressId,
            Long categoryId,
            Long workerId,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            String bookingStatus) {

        this.bookingId = bookingId;
        this.addressId = addressId;
        this.categoryId = categoryId;
        this.workerId = workerId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }
}
