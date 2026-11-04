package com.example.demo.dto;

public class LoginResponse {

    private Long userId;
    private String fullName;
    private String role;
    private String message;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(
            Long userId,
            String fullName,
            String role,
            String message,
            String token) {

        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}