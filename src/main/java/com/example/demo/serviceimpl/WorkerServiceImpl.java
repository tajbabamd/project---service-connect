package com.example.demo.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WorkerRegistrationRequest;

import com.example.demo.entity.User;
import com.example.demo.entity.Worker;
import com.example.demo.entity.Role;
import com.example.demo.entity.AccountStatus;
import com.example.demo.entity.AvailabilityStatus;
import com.example.demo.entity.VerificationStatus;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkerRepository;

import com.example.demo.service.WorkerService;

@Service
public class WorkerServiceImpl implements WorkerService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkerServiceImpl(
            UserRepository userRepository,
            WorkerRepository workerRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String registerWorker(WorkerRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered");
        }

        if (userRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException(
                    "Mobile already registered");
        }

        if (request.getExperienceYears() == null
                || request.getExperienceYears() < 0) {
            throw new IllegalArgumentException(
                    "Experience years must be zero or greater");
        }

        // Step 1: Create the user account

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.WORKER);
        user.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Step 2: Create the worker profile

        Worker worker = new Worker();

        worker.setUser(savedUser);
        worker.setCity(request.getCity());
        worker.setState(request.getState());
        worker.setPincode(request.getPincode());

        worker.setExperienceYears(request.getExperienceYears());
        worker.setDescription(request.getDescription());

        worker.setAvailabilityStatus(
                AvailabilityStatus.UNAVAILABLE);

        worker.setVerificationStatus(
                VerificationStatus.PENDING);

        workerRepository.save(worker);

        return "Worker registered successfully. Awaiting admin verification.";
    }
}