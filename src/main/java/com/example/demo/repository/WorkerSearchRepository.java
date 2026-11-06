
package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.example.demo.dto.WorkerSearchResult;
import com.example.demo.entity.WorkerSkill;

public interface WorkerSearchRepository
        extends Repository<WorkerSkill, Long> {

    @Query("""
        select new com.example.demo.dto.WorkerSearchResult(
            worker.workerId,
            account.fullName,
            worker.experienceYears,
            skill.skillDescription
        )
        from WorkerSkill skill
        join skill.worker worker
        join worker.user account

        where skill.category.categoryId = :categoryId
          and skill.category.isActive = true

          and worker.verificationStatus =
              com.example.demo.entity.VerificationStatus.APPROVED

          and worker.availabilityStatus =
              com.example.demo.entity.AvailabilityStatus.AVAILABLE

          and account.accountStatus =
              com.example.demo.entity.AccountStatus.ACTIVE

          and exists (
              select area.serviceAreaId
              from WorkerServiceArea area
              where area.worker = worker
                and area.pincode = :pincode
          )

          and exists (
              select hours.availabilityId
              from WorkerAvailability hours
              where hours.worker = worker
                and hours.dayOfWeek = :dayOfWeek
                and hours.isActive = true
                and hours.startTime <= :startTime
                and hours.endTime >= :endTime
          )

          and not exists (
              select timeOff.timeOffId
              from WorkerTimeOff timeOff
              where timeOff.worker = worker
                and timeOff.startDatetime < :requestedEnd
                and timeOff.endDatetime > :requestedStart
          )

          and not exists (
              select booked.bookingId
              from Booking booked
              where booked.worker = worker
                and booked.bookingDate = :bookingDate
                and booked.startTime < :endTime
                and booked.endTime > :startTime
                and booked.bookingStatus in (
                    com.example.demo.entity.BookingStatus.ASSIGNED,
                    com.example.demo.entity.BookingStatus.IN_PROGRESS,
                    com.example.demo.entity.BookingStatus.COMPLETED
                )
          )

        order by worker.workerId
        """)
    List<WorkerSearchResult> findEligibleWorkers(
            @Param("categoryId") Long categoryId,
            @Param("pincode") String pincode,
            @Param("dayOfWeek") int dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd);
}
