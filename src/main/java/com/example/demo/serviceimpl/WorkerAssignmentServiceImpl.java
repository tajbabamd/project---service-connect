
package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WorkerSearchResult;

import com.example.demo.entity.AccountStatus;
import com.example.demo.entity.AvailabilityStatus;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.entity.Worker;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.WorkerRepository;
import com.example.demo.repository.WorkerSearchRepository;
import com.example.demo.repository.WorkerTimeOffRepository;

import com.example.demo.service.WorkerAssignmentService;

@Service
public class WorkerAssignmentServiceImpl
        implements WorkerAssignmentService {

    private static final ZoneId SERVICE_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final WorkerSearchRepository searchRepository;
    private final WorkerRepository workerRepository;
    private final BookingRepository bookingRepository;
    private final WorkerTimeOffRepository timeOffRepository;

    public WorkerAssignmentServiceImpl(
            WorkerSearchRepository searchRepository,
            WorkerRepository workerRepository,
            BookingRepository bookingRepository,
            WorkerTimeOffRepository timeOffRepository) {

        this.searchRepository = searchRepository;
        this.workerRepository = workerRepository;
        this.bookingRepository = bookingRepository;
        this.timeOffRepository = timeOffRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Booking assignWorker(Booking booking) {

        // STEP 1: Only pending or unassigned bookings
        // can be considered for worker assignment.

        if (booking.getBookingStatus() != BookingStatus.PENDING
                && booking.getBookingStatus() != BookingStatus.UNASSIGNED) {

            throw new IllegalStateException(
                    "Booking is not eligible for assignment");
        }

        // STEP 2: Convert date and times into
        // LocalDateTime values.

        LocalDateTime requestedStart = LocalDateTime.of(
                booking.getBookingDate(),
                booking.getStartTime());

        LocalDateTime requestedEnd = LocalDateTime.of(
                booking.getBookingDate(),
                booking.getEndTime());

        // STEP 3: Find workers matching the category,
        // PIN code and schedule conditions.

        List<WorkerSearchResult> candidates =
                searchRepository.findEligibleWorkers(
                        booking.getCategory().getCategoryId(),
                        booking.getAddress().getPincode(),
                        booking.getBookingDate()
                                .getDayOfWeek().getValue(),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        booking.getBookingDate(),
                        requestedStart,
                        requestedEnd
                );

			if (!requestedStart.isAfter(
			        LocalDateTime.now(SERVICE_ZONE))) {
			
			    throw new IllegalStateException(
			            "Cannot assign a booking whose start time has passed");
			}


        // STEP 4: Check candidates one by one.
        for (WorkerSearchResult candidate : candidates) {

            // Lock this worker's row before assigning.
            Worker worker = workerRepository
                    .findByIdForUpdate(candidate.workerId())
                    .orElse(null);

            if (worker == null) {
                continue;
            }

            // STEP 5: Recheck the worker's current status.
            if (worker.getVerificationStatus()
                        != VerificationStatus.APPROVED
                    || worker.getAvailabilityStatus()
                        != AvailabilityStatus.AVAILABLE
                    || worker.getUser().getAccountStatus()
                        != AccountStatus.ACTIVE) {

                continue;
            }

            // STEP 6: Recheck overlapping time off
            // after locking the worker.
            if (!timeOffRepository
                    .findOverlappingTimeOffForUpdate(
                            worker.getWorkerId(),
                            requestedStart,
                            requestedEnd)
                    .isEmpty()) {

                continue;
            }

            // STEP 7: Check already reserved appointments.
            List<Booking> conflicts = bookingRepository
                    .findConflictingBookingsForUpdate(
                            worker.getWorkerId(),
                            booking.getBookingDate(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            List.of(
                                    BookingStatus.ASSIGNED,
                                    BookingStatus.IN_PROGRESS,
                                    BookingStatus.COMPLETED
                            )
                    );

            if (!conflicts.isEmpty()) {
                continue;
            }

            // STEP 8: Assign the eligible worker.
            booking.setWorker(worker);
            booking.setBookingStatus(BookingStatus.ASSIGNED);
            booking.setAssignedAt(
                    LocalDateTime.now(SERVICE_ZONE));

            return bookingRepository.saveAndFlush(booking);
        }

        // STEP 9: No worker matched all conditions.
        booking.setWorker(null);
        booking.setBookingStatus(BookingStatus.UNASSIGNED);

        return bookingRepository.saveAndFlush(booking);
    }
}
