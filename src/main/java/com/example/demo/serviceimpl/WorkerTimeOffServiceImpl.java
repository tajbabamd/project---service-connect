package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddWorkerTimeOffRequest;
import com.example.demo.dto.WorkerTimeOffResponse;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Worker;
import com.example.demo.entity.WorkerTimeOff;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.WorkerRepository;
import com.example.demo.repository.WorkerTimeOffRepository;

import com.example.demo.service.WorkerTimeOffService;

@Service
public class WorkerTimeOffServiceImpl
        implements WorkerTimeOffService {

    private final WorkerRepository workerRepository;
    private final WorkerTimeOffRepository timeOffRepository;
    private final BookingRepository bookingRepository;

    public WorkerTimeOffServiceImpl(
            WorkerRepository workerRepository,
            WorkerTimeOffRepository timeOffRepository,
            BookingRepository bookingRepository) {

        this.workerRepository = workerRepository;
        this.timeOffRepository = timeOffRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public WorkerTimeOffResponse addTimeOff(
            Long userId,
            AddWorkerTimeOffRequest request) {

        // Step 1: Validate request
        if (request == null
                || request.getStartDatetime() == null
                || request.getEndDatetime() == null) {

            throw new IllegalArgumentException(
                    "Start date/time and end date/time are required");
        }

        LocalDateTime start = request.getStartDatetime();
        LocalDateTime end = request.getEndDatetime();

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(
                    "Start date/time must be before end date/time");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Time off cannot start in the past");
        }

        String reason = request.getReason();

        if (reason != null) {
            reason = reason.trim();

            if (reason.length() > 255) {
                throw new IllegalArgumentException(
                        "Reason must not exceed 255 characters");
            }
        }

        // Step 2: Identify the authenticated worker
        Worker worker = getWorker(userId);
        
        //added in p3 s8.2
        workerRepository.findByIdForUpdate(worker.getWorkerId())
        	.orElseThrow(() ->
        	new NoSuchElementException(
                        "Worker profile not found"));


        // Step 3: Check existing time-off overlap
        boolean overlaps = timeOffRepository
            .existsByWorker_WorkerIdAndStartDatetimeLessThanAndEndDatetimeGreaterThan(
                worker.getWorkerId(),
                end,
                start
            );

        if (overlaps) {
            throw new IllegalStateException(
                    "Time off overlaps with an existing leave period");
        }

        // Step 4: Check already assigned bookings
//        List<Booking> bookings = bookingRepository
//                .findByWorker_WorkerId(worker.getWorkerId());
        //above lines replased with below 7 lines

          List<Booking> bookings = bookingRepository
        		  .findBusyBookingsForUpdate(
        				  worker.getWorkerId(),
        				  List.of(
        						  BookingStatus.ASSIGNED,
        						  BookingStatus.IN_PROGRESS
        				  ));


        for (Booking booking : bookings) {

            if (booking.getBookingStatus() != BookingStatus.ASSIGNED
                    && booking.getBookingStatus()
                            != BookingStatus.IN_PROGRESS) {
                continue;
            }

            LocalDateTime bookingStart =
                    booking.getBookingDate()
                            .atTime(booking.getStartTime());

            LocalDateTime bookingEnd =
                    booking.getBookingDate()
                            .atTime(booking.getEndTime());

            boolean bookingConflict =
                    start.isBefore(bookingEnd)
                    && end.isAfter(bookingStart);

            if (bookingConflict) {
                throw new IllegalStateException(
                        "Time off conflicts with an existing booking");
            }
        }

        // Step 5: Create time-off record
        WorkerTimeOff timeOff = new WorkerTimeOff();

        timeOff.setWorker(worker);
        timeOff.setStartDatetime(start);
        timeOff.setEndDatetime(end);
        timeOff.setReason(reason);

        // Step 6: Save to MySQL
        WorkerTimeOff saved = timeOffRepository.save(timeOff);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerTimeOffResponse> getMyTimeOff(Long userId) {

        Worker worker = getWorker(userId);

        List<WorkerTimeOff> records =
                timeOffRepository.findByWorker_WorkerId(
                        worker.getWorkerId());

        List<WorkerTimeOffResponse> response =
                new ArrayList<>();

        for (WorkerTimeOff record : records) {
            response.add(toResponse(record));
        }

        return response;
    }

    @Override
    @Transactional
    public String deleteTimeOff(Long userId, Long timeOffId) {

        Worker worker = getWorker(userId);

        WorkerTimeOff timeOff = timeOffRepository
                .findByTimeOffIdAndWorker_WorkerId(
                        timeOffId,
                        worker.getWorkerId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Time-off record not found"));

        timeOffRepository.delete(timeOff);

        return "Time-off record deleted successfully";
    }

    private Worker getWorker(Long userId) {

        return workerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker profile not found"));
    }

    private WorkerTimeOffResponse toResponse(
            WorkerTimeOff timeOff) {

        return new WorkerTimeOffResponse(
                timeOff.getTimeOffId(),
                timeOff.getStartDatetime(),
                timeOff.getEndDatetime(),
                timeOff.getReason()
        );
    }
}