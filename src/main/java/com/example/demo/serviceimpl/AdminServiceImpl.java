package com.example.demo.serviceimpl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PendingWorkerResponse;
import com.example.demo.entity.AccountStatus;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.entity.Worker;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkerRepository;
import com.example.demo.service.AdminService;

//new imports at phase 3 step 5.3 called a

import com.example.demo.entity.DocumentStatus;
import com.example.demo.repository.WorkerDocumentRepository;

@Service
public class AdminServiceImpl implements AdminService {

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    //a 
    private final WorkerDocumentRepository documentRepository;

    public AdminServiceImpl(
            WorkerRepository workerRepository,
            UserRepository userRepository,
            //a
    		WorkerDocumentRepository documentRepository){

        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
        //a
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingWorkerResponse> getPendingWorkers() {

        List<Worker> workers =
                workerRepository.findByVerificationStatus(
                        VerificationStatus.PENDING);

        List<PendingWorkerResponse> response =
                new ArrayList<>();

        for (Worker worker : workers) {

            response.add(new PendingWorkerResponse(
                    worker.getWorkerId(),
                    worker.getUser().getFullName(),
                    worker.getUser().getEmail(),
                    worker.getCity(),
                    worker.getExperienceYears(),
                    worker.getDescription(),
                    worker.getVerificationStatus().name()
            ));
        }

        return response;
    }

    @Override
    @Transactional
    public String approveWorker(Long workerId, Long adminUserId) {

        Worker worker = getPendingWorker(workerId);
        //a
        boolean hasVerifiedDocument = documentRepository
                .findByWorker_WorkerId(workerId)
                .stream()
                .anyMatch(document ->
                        document.getDocumentStatus()
                                == DocumentStatus.VERIFIED);

        if (!hasVerifiedDocument) {
            throw new IllegalStateException(
                    "Worker must have a verified identity document before approval");
        }

        User admin = getAdminUser(adminUserId);

        worker.setVerificationStatus(
                VerificationStatus.APPROVED);

        worker.setVerifiedBy(admin);
        worker.setVerifiedAt(LocalDateTime.now());
        worker.setRejectionReason(null);

        workerRepository.save(worker);

        return "Worker approved successfully";
    }

    @Override
    @Transactional
    public String rejectWorker(
            Long workerId,
            Long adminUserId,
            String reason) {

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required");
        }

        String trimmedReason = reason.trim();

        if (trimmedReason.length() > 255) {
            throw new IllegalArgumentException(
                    "Rejection reason must not exceed 255 characters");
        }

        Worker worker = getPendingWorker(workerId);
        User admin = getAdminUser(adminUserId);

        worker.setVerificationStatus(
                VerificationStatus.REJECTED);

        worker.setVerifiedBy(admin);
        worker.setVerifiedAt(LocalDateTime.now());
        worker.setRejectionReason(trimmedReason);

        workerRepository.save(worker);

        return "Worker rejected successfully";
    }

    private Worker getPendingWorker(Long workerId) {

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Worker not found"));

        if (worker.getVerificationStatus()
                != VerificationStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending workers can be reviewed");
        }

        return worker;
    }

    private User getAdminUser(Long adminUserId) {

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Administrator not found"));

        if (admin.getRole() != Role.ADMIN
                || admin.getAccountStatus() != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Invalid administrator account");
        }

        return admin;
    }
}