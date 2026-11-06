
package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Worker;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.WorkerRepository;

import com.example.demo.service.WorkerBookingService;

@Service
public class WorkerBookingServiceImpl
        implements WorkerBookingService {

    private static final ZoneId SERVICE_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final WorkerRepository workerRepository;
    private final BookingRepository bookingRepository;

    public WorkerBookingServiceImpl(
            WorkerRepository workerRepository,
            BookingRepository bookingRepository) {

        this.workerRepository = workerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {

        Worker worker = getWorker(userId);

        List<Booking> bookings = bookingRepository
                .findByWorker_WorkerIdOrderByBookingIdDesc(
                        worker.getWorkerId());

        List<BookingResponse> response = new ArrayList<>();

        for (Booking booking : bookings) {
            response.add(toResponse(booking));
        }

        return response;
    }

    @Override
    @Transactional
    public BookingResponse startBooking(
            Long userId, Long bookingId) {

        Worker worker = getWorker(userId);

        Booking booking = getOwnedBookingForUpdate(
                worker.getWorkerId(), bookingId);

        if (booking.getBookingStatus()
                != BookingStatus.ASSIGNED) {

            throw new IllegalStateException(
                    "Only assigned bookings can be started");
        }

        LocalDateTime now = LocalDateTime.now(SERVICE_ZONE);

        LocalDateTime scheduledStart = LocalDateTime.of(
                booking.getBookingDate(),
                booking.getStartTime());

        LocalDateTime scheduledEnd = LocalDateTime.of(
                booking.getBookingDate(),
                booking.getEndTime());

        if (now.isBefore(scheduledStart)) {
            throw new IllegalStateException(
                    "Appointment has not started yet");
        }

        if (!now.isBefore(scheduledEnd)) {
            throw new IllegalStateException(
                    "Appointment window has already ended");
        }

        booking.setBookingStatus(BookingStatus.IN_PROGRESS);

        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(
            Long userId, Long bookingId) {

        Worker worker = getWorker(userId);

        Booking booking = getOwnedBookingForUpdate(
                worker.getWorkerId(), bookingId);

        if (booking.getBookingStatus()
                != BookingStatus.IN_PROGRESS) {

            throw new IllegalStateException(
                    "Only in-progress bookings can be completed");
        }

        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(
                LocalDateTime.now(SERVICE_ZONE));

        return toResponse(bookingRepository.save(booking));
    }

    private Worker getWorker(Long userId) {

        return workerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker profile not found"));
    }

    private Booking getOwnedBookingForUpdate(
            Long workerId, Long bookingId) {

        Booking booking = bookingRepository
                .findByIdForUpdate(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found"));

        if (booking.getWorker() == null
                || !booking.getWorker().getWorkerId()
                        .equals(workerId)) {

            throw new NoSuchElementException(
                    "Booking not found");
        }

        return booking;
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
