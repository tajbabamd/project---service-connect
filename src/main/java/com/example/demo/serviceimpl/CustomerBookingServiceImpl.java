
package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.CancelBookingRequest;
import com.example.demo.dto.CreateBookingRequest;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;
import com.example.demo.entity.ServiceCategory;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CustomerAddressRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ServiceCategoryRepository;

import com.example.demo.service.CustomerBookingService;
import com.example.demo.service.WorkerAssignmentService;

@Service
public class CustomerBookingServiceImpl
        implements CustomerBookingService {

    private static final ZoneId SERVICE_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final WorkerAssignmentService assignmentService;

    public CustomerBookingServiceImpl(
            BookingRepository bookingRepository,
            CustomerRepository customerRepository,
            CustomerAddressRepository addressRepository,
            ServiceCategoryRepository categoryRepository,
            WorkerAssignmentService assignmentService) {

        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.categoryRepository = categoryRepository;
        this.assignmentService = assignmentService;
    }

    // STEP 8.1 + 8.2: Create and assign a booking
    @Override
    @Transactional
    public BookingResponse createBooking(
            Long userId,
            CreateBookingRequest request) {

        validateRequest(request);

        Customer customer = getCustomer(userId);

        CustomerAddress address = addressRepository
                .findByAddressIdAndCustomer_CustomerId(
                        request.getAddressId(),
                        customer.getCustomerId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Customer address not found"));

        ServiceCategory category = categoryRepository
                .findById(request.getCategoryId())
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Active service category not found"));

        Booking booking = new Booking();

        booking.setCustomer(customer);
        booking.setAddress(address);
        booking.setCategory(category);

        booking.setServiceDescription(
                request.getServiceDescription().trim());

        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());

        booking.setWorker(null);
        booking.setBookingStatus(BookingStatus.PENDING);

        // First save the new booking.
        Booking saved = bookingRepository.saveAndFlush(booking);

        // Then attempt automatic assignment.
        Booking assigned = assignmentService.assignWorker(saved);

        return toResponse(assigned);
    }

    // STEP 8.3: View my booking history
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {

        Customer customer = getCustomer(userId);

        List<Booking> bookings = bookingRepository
                .findByCustomer_CustomerIdOrderByBookingIdDesc(
                        customer.getCustomerId());

        List<BookingResponse> response = new ArrayList<>();

        for (Booking booking : bookings) {
            response.add(toResponse(booking));
        }

        return response;
    }

    // STEP 8.3: View a specific booking
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(
            Long userId, Long bookingId) {

        Customer customer = getCustomer(userId);

//        Booking booking = getOwnedBooking(
//                customer.getCustomerId(), bookingId);
        

			Booking booking = bookingRepository
			        .findByIdForUpdate(bookingId)
			        .orElseThrow(() ->
			                new NoSuchElementException(
			                        "Booking not found"));
			
			if (!booking.getCustomer().getCustomerId()
			        .equals(customer.getCustomerId())) {
			
			    throw new NoSuchElementException(
			            "Booking not found");
			}


        return toResponse(booking);
    }

    // STEP 8.3: Customer cancels a booking
    @Override
    @Transactional
    public BookingResponse cancelBooking(
            Long userId,
            Long bookingId,
            CancelBookingRequest request) {

        Customer customer = getCustomer(userId);

        Booking booking = getOwnedBooking(
                customer.getCustomerId(), bookingId);

        BookingStatus status = booking.getBookingStatus();

        if (status != BookingStatus.PENDING
                && status != BookingStatus.UNASSIGNED
                && status != BookingStatus.ASSIGNED) {

            throw new IllegalStateException(
                    "This booking cannot be cancelled");
        }

        LocalDateTime bookingStart = LocalDateTime.of(
                booking.getBookingDate(),
                booking.getStartTime());

        if (!bookingStart.isAfter(
                LocalDateTime.now(SERVICE_ZONE))) {

            throw new IllegalStateException(
                    "Cannot cancel after appointment start time");
        }

        String reason = "Cancelled by customer";

        if (request != null
                && request.getReason() != null
                && !request.getReason().isBlank()) {

            reason = request.getReason().trim();
        }

        if (reason.length() > 255) {
            throw new IllegalArgumentException(
                    "Cancellation reason must not exceed 255 characters");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);

        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    private Customer getCustomer(Long userId) {

        return customerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Customer profile not found"));
    }

    private Booking getOwnedBooking(
            Long customerId, Long bookingId) {

        return bookingRepository
                .findByBookingIdAndCustomer_CustomerId(
                        bookingId, customerId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found"));
    }

    private void validateRequest(
            CreateBookingRequest request) {

        if (request == null
                || request.getAddressId() == null
                || request.getCategoryId() == null
                || request.getBookingDate() == null
                || request.getStartTime() == null
                || request.getEndTime() == null
                || request.getServiceDescription() == null
                || request.getServiceDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "All booking fields are required");
        }

        if (request.getAddressId() <= 0
                || request.getCategoryId() <= 0) {

            throw new IllegalArgumentException(
                    "Address and category IDs must be positive");
        }

        if (request.getServiceDescription().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Service description must not exceed 255 characters");
        }

        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        LocalDateTime requestedStart = LocalDateTime.of(
                request.getBookingDate(),
                request.getStartTime());

        if (!requestedStart.isAfter(
                LocalDateTime.now(SERVICE_ZONE))) {

            throw new IllegalArgumentException(
                    "Booking must start in the future");
        }
    }

    private BookingResponse toResponse(Booking booking) {

        Long workerId = null;

        if (booking.getWorker() != null) {
            workerId = booking.getWorker().getWorkerId();
        }

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
