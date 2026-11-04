package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CustomerAddress;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomer_CustomerId(Long customerId);

    Optional<CustomerAddress> findByCustomer_CustomerIdAndIsDefaultTrue(
            Long customerId);

    Optional<CustomerAddress> findByAddressIdAndCustomer_CustomerId(
            Long addressId, Long customerId);
}