
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.BookingResponse;

public interface WorkerBookingService {

    List<BookingResponse> getMyBookings(Long userId);

    BookingResponse startBooking(
            Long userId, Long bookingId);

    BookingResponse completeBooking(
            Long userId, Long bookingId);
}
