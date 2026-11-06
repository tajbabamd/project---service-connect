package com.example.demo.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.dao.DataIntegrityViolationException;

import com.example.demo.dto.CustomerAddressRequest;
import com.example.demo.dto.CustomerAddressResponse;

import com.example.demo.entity.User;
import com.example.demo.service.CustomerAddressService;

@RestController
@RequestMapping("/api/customers/addresses")
public class CustomerAddressController {

    private final CustomerAddressService addressService;

    public CustomerAddressController(
            CustomerAddressService addressService) {

        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<CustomerAddressResponse> addAddress(
            @AuthenticationPrincipal User user,
            @RequestBody CustomerAddressRequest request) {

        CustomerAddressResponse response =
                addressService.addAddress(
                        user.getUserId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<CustomerAddressResponse> getMyAddresses(
            @AuthenticationPrincipal User user) {

        return addressService.getMyAddresses(user.getUserId());
    }

    @PutMapping("/{addressId}")
    public CustomerAddressResponse updateAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId,
            @RequestBody CustomerAddressRequest request) {

        return addressService.updateAddress(
                user.getUserId(), addressId, request);
    }

    @PutMapping("/{addressId}/default")
    public CustomerAddressResponse setDefaultAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId) {

        return addressService.setDefaultAddress(
                user.getUserId(), addressId);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.deleteAddress(
                        user.getUserId(), addressId));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(
            NoSuchElementException e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(
            IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler({
            IllegalStateException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<String> conflict(Exception e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Address operation conflicts with existing data");
    }
}