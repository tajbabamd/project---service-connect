package com.example.demo.repository;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;


import java.util.Collection;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface BookingRepository extends JpaRepository<Booking, Long> {

	// Find all bookings belonging to a customer
	List<Booking> findByCustomer_CustomerId(Long customerId);

	// Find all bookings assigned to a worker
	List<Booking> findByWorker_WorkerId(Long workerId);

	// Find bookings with a particular status
	List<Booking> findByBookingStatus(BookingStatus bookingStatus);

	// Find a specific booking belonging to a customer
	Optional<Booking> findByBookingIdAndCustomer_CustomerId(Long bookingId, Long customerId);

	// Check whether a worker has a conflicting booking
	boolean existsByWorker_WorkerIdAndBookingDateAndBookingStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
			Long workerId, LocalDate bookingDate, List<BookingStatus> statuses, LocalTime requestedEnd,
			LocalTime requestedStart);

	boolean existsByAddress_AddressId(Long addressId);

	List<Booking> findByCustomer_CustomerIdOrderByBookingIdDesc(Long customerId);

	List<Booking> findByWorker_WorkerIdOrderByBookingIdDesc(Long workerId);

	Optional<Booking> findByBookingIdAndWorker_WorkerId(Long bookingId, Long workerId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			select b from Booking b
			where b.bookingId = :bookingId
			""")
	Optional<Booking> findByIdForUpdate(@Param("bookingId") Long bookingId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			select b from Booking b
			where b.worker.workerId = :workerId
			  and b.bookingDate = :bookingDate
			  and b.startTime < :endTime
			  and b.endTime > :startTime
			  and b.bookingStatus in :statuses
			""")
	List<Booking> findConflictingBookingsForUpdate(@Param("workerId") Long workerId,
			@Param("bookingDate") LocalDate bookingDate, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime, @Param("statuses") Collection<BookingStatus> statuses);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("""
			select b from Booking b
			where b.worker.workerId = :workerId
			  and b.bookingStatus in :statuses
			""")
	List<Booking> findBusyBookingsForUpdate(@Param("workerId") Long workerId,
			@Param("statuses") Collection<BookingStatus> statuses);

}