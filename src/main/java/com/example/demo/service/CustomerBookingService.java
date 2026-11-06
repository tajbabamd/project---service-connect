
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.CreateBookingRequest;
import com.example.demo.dto.CancelBookingRequest;

public interface CustomerBookingService {

    BookingResponse createBooking(
            Long userId, CreateBookingRequest request);

    List<BookingResponse> getMyBookings(Long userId);

    BookingResponse getBooking(
            Long userId, Long bookingId);

    BookingResponse cancelBooking(
            Long userId,
            Long bookingId,
            CancelBookingRequest request);
}
