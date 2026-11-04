package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}