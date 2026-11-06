
package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.BookingResponse;
import com.example.demo.service.AdminBookingService;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final AdminBookingService bookingService;

    public AdminBookingController(
            AdminBookingService bookingService) {

        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getAllBookings() {

        return bookingService.getAllBookings();
    }

    @PutMapping("/{bookingId}/retry-assignment")
    public BookingResponse retryAssignment(
            @PathVariable Long bookingId) {

        return bookingService.retryAssignment(bookingId);
    }
}
