package com.example.demo.dto;

public class PendingWorkerResponse {

    private Long workerId;
    private String fullName;
    private String email;
    private String city;
    private Integer experienceYears;
    private String description;
    private String verificationStatus;

    public PendingWorkerResponse(
            Long workerId,
            String fullName,
            String email,
            String city,
            Integer experienceYears,
            String description,
            String verificationStatus) {

        this.workerId = workerId;
        this.fullName = fullName;
        this.email = email;
        this.city = city;
        this.experienceYears = experienceYears;
        this.description = description;
        this.verificationStatus = verificationStatus;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public String getDescription() {
        return description;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}