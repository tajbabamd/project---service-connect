package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Role;
import com.example.demo.entity.AccountStatus;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AdminRepository;

@Component
@ConditionalOnProperty(
    name = "app.bootstrap-admin.enabled",
    havingValue = "true"
)
public class AdminBootstrapRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${BOOTSTRAP_ADMIN_FULL_NAME}")
    private String fullName;

    @Value("${BOOTSTRAP_ADMIN_EMAIL}")
    private String email;

    @Value("${BOOTSTRAP_ADMIN_MOBILE}")
    private String mobile;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD}")
    private String password;

    public AdminBootstrapRunner(
            UserRepository userRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.existsByEmail(email)
                || userRepository.existsByMobile(mobile)) {

            throw new IllegalStateException(
                "Admin email or mobile already registered. "
                + "Disable bootstrap if admin was already created."
            );
        }

        if (password.length() < 12) {
            throw new IllegalArgumentException(
                "Admin password must contain at least 12 characters"
            );
        }

        // Step 1: Create administrator user

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobile(mobile);

        user.setPasswordHash(
            passwordEncoder.encode(password)
        );

        user.setRole(Role.ADMIN);
        user.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Step 2: Create administrator profile

        Admin admin = new Admin();

        admin.setUser(savedUser);
        admin.setDesignation("System Administrator");

        adminRepository.save(admin);

        System.out.println(
            "Initial administrator created successfully."
        );
    }
}