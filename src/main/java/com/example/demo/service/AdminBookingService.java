
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.BookingResponse;

public interface AdminBookingService {

    List<BookingResponse> getAllBookings();

    BookingResponse retryAssignment(Long bookingId);
}
