
package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;

import com.example.demo.repository.BookingRepository;
import com.example.demo.service.AdminBookingService;
import com.example.demo.service.WorkerAssignmentService;

@Service
public class AdminBookingServiceImpl
        implements AdminBookingService {

    private final BookingRepository bookingRepository;
    private final WorkerAssignmentService assignmentService;

    public AdminBookingServiceImpl(
            BookingRepository bookingRepository,
            WorkerAssignmentService assignmentService) {

        this.bookingRepository = bookingRepository;
        this.assignmentService = assignmentService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {

        List<Booking> bookings = bookingRepository.findAll(
                Sort.by(Sort.Direction.DESC, "bookingId"));

        List<BookingResponse> response = new ArrayList<>();

        for (Booking booking : bookings) {
            response.add(toResponse(booking));
        }

        return response;
    }

    @Override
    @Transactional
    public BookingResponse retryAssignment(Long bookingId) {

        Booking booking = bookingRepository
                .findByIdForUpdate(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.UNASSIGNED
                && booking.getBookingStatus() != BookingStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending or unassigned bookings can be retried");
        }

        Booking updated = assignmentService.assignWorker(booking);

        return toResponse(updated);
    }

    private BookingResponse toResponse(Booking booking) {

        Long workerId = booking.getWorker() == null
                ? null
                : booking.getWorker().getWorkerId();

        return new BookingResponse(
                booking.getBookingId(),
                booking.getAddress().getAddressId(),
                booking.getCategory().getCategoryId(),
                workerId,
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus().name()
        );
    }
}
