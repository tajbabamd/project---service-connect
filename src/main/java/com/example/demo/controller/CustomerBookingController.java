
package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.CancelBookingRequest;
import com.example.demo.dto.CreateBookingRequest;
import com.example.demo.entity.User;
import com.example.demo.service.CustomerBookingService;

@RestController
@RequestMapping("/api/customers/bookings")
public class CustomerBookingController {

    private final CustomerBookingService bookingService;

    public CustomerBookingController(
            CustomerBookingService bookingService) {

        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal User user,
            @RequestBody CreateBookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(
                        user.getUserId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<BookingResponse> getMyBookings(
            @AuthenticationPrincipal User user) {

        return bookingService.getMyBookings(user.getUserId());
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId) {

        return bookingService.getBooking(
                user.getUserId(), bookingId);
    }

    @PutMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long bookingId,
            @RequestBody(required = false)
                    CancelBookingRequest request) {

        return bookingService.cancelBooking(
                user.getUserId(), bookingId, request);
    }
}
