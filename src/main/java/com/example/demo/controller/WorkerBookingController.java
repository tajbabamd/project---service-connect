
package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.User;
import com.example.demo.service.WorkerBookingService;

@RestController
@RequestMapping("/api/workers/bookings")
public class WorkerBookingController {

    private final WorkerBookingService bookingService;

    public WorkerBookingController(
            WorkerBookingService bookingService) {

        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getMyBookings(
            @AuthenticationPrincipal User user) {

        return bookingService.getMyBookings(user.getUserId());
    }

    @PutMapping("/{bookingId}/start")
    public BookingResponse startBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {

        return bookingService.startBooking(
                user.getUserId(), bookingId);
    }

    @PutMapping("/{bookingId}/complete")
    public BookingResponse completeBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {

        return bookingService.completeBooking(
                user.getUserId(), bookingId);
    }
}
