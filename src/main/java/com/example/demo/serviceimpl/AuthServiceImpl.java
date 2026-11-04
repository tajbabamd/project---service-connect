package com.example.demo.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;

import com.example.demo.entity.User;
import com.example.demo.entity.AccountStatus;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // Step 1: Find user by email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Invalid email or password"
                    )
                );

        // Step 2: Verify BCrypt password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                "Invalid email or password"
            );
        }

        // Step 3: Check account status
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Account is not active"
            );
        }

        // Step 4: Generate JWT token
        String token = jwtService.generateToken(user);

        // Step 5: Return login response
        return new LoginResponse(
                user.getUserId(),
                user.getFullName(),
                user.getRole().name(),
                "Login successful",
                token
        );
    }
}