package com.example.demo.service;

import com.example.demo.dto.CustomerRegistrationRequest;

public interface CustomerService {

    String registerCustomer(CustomerRegistrationRequest request);
}