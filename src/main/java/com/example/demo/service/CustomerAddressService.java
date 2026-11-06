package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CustomerAddressRequest;
import com.example.demo.dto.CustomerAddressResponse;

public interface CustomerAddressService {

    CustomerAddressResponse addAddress(
            Long userId,
            CustomerAddressRequest request);

    List<CustomerAddressResponse> getMyAddresses(Long userId);

    CustomerAddressResponse updateAddress(
            Long userId,
            Long addressId,
            CustomerAddressRequest request);

    CustomerAddressResponse setDefaultAddress(
            Long userId,
            Long addressId);

    String deleteAddress(Long userId, Long addressId);
}