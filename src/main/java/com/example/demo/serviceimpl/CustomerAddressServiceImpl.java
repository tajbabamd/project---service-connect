package com.example.demo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CustomerAddressRequest;
import com.example.demo.dto.CustomerAddressResponse;

import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.CustomerAddressRepository;
import com.example.demo.repository.CustomerRepository;

import com.example.demo.service.CustomerAddressService;

@Service
public class CustomerAddressServiceImpl
        implements CustomerAddressService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final BookingRepository bookingRepository;

    public CustomerAddressServiceImpl(
            CustomerRepository customerRepository,
            CustomerAddressRepository addressRepository,
            BookingRepository bookingRepository) {

        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.bookingRepository = bookingRepository;
    }

    // 1. Add a new address
    @Override
    @Transactional
    public CustomerAddressResponse addAddress(
            Long userId,
            CustomerAddressRequest request) {

        validateRequest(request);

        Customer customer = getCustomer(userId);

        List<CustomerAddress> existingAddresses =
                addressRepository.findByCustomer_CustomerId(
                        customer.getCustomerId());

        CustomerAddress address = new CustomerAddress();

        address.setCustomer(customer);
        copyDetails(request, address);

        // The first address becomes the default.
        address.setIsDefault(existingAddresses.isEmpty());

        CustomerAddress saved = addressRepository.save(address);

        return toResponse(saved);
    }

    // 2. View all saved addresses
    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getMyAddresses(
            Long userId) {

        Customer customer = getCustomer(userId);

        List<CustomerAddress> addresses =
                addressRepository.findByCustomer_CustomerId(
                        customer.getCustomerId());

        List<CustomerAddressResponse> response =
                new ArrayList<>();

        for (CustomerAddress address : addresses) {
            response.add(toResponse(address));
        }

        return response;
    }

    // 3. Update an existing address
    @Override
    @Transactional
    public CustomerAddressResponse updateAddress(
            Long userId,
            Long addressId,
            CustomerAddressRequest request) {

        validateRequest(request);

        Customer customer = getCustomer(userId);

        CustomerAddress address =
                getOwnedAddress(customer.getCustomerId(), addressId);

        // Protect addresses already used in bookings.
        checkNotUsedInBooking(addressId);

        copyDetails(request, address);

        CustomerAddress saved = addressRepository.save(address);

        return toResponse(saved);
    }

    // 4. Change the default address
    @Override
    @Transactional
    public CustomerAddressResponse setDefaultAddress(
            Long userId,
            Long addressId) {

        Customer customer = getCustomer(userId);

        Long customerId = customer.getCustomerId();

        CustomerAddress selected =
                getOwnedAddress(customerId, addressId);

        addressRepository
                .findByCustomer_CustomerIdAndIsDefaultTrue(customerId)
                .ifPresent(current -> {
                    current.setIsDefault(false);
                    addressRepository.save(current);
                });

        selected.setIsDefault(true);

        CustomerAddress saved =
                addressRepository.save(selected);

        return toResponse(saved);
    }

    // 5. Delete an address
    @Override
    @Transactional
    public String deleteAddress(Long userId, Long addressId) {

        Customer customer = getCustomer(userId);

        Long customerId = customer.getCustomerId();

        CustomerAddress address =
                getOwnedAddress(customerId, addressId);

        checkNotUsedInBooking(addressId);

        List<CustomerAddress> addresses =
                addressRepository.findByCustomer_CustomerId(customerId);

        // Keep a default address if other addresses exist.
        if (Boolean.TRUE.equals(address.getIsDefault())
                && addresses.size() > 1) {

            throw new IllegalStateException(
                    "Set another default address before deleting this one");
        }

        addressRepository.delete(address);

        return "Customer address deleted successfully";
    }

    // Find customer from the JWT user ID
    private Customer getCustomer(Long userId) {

        return customerRepository.findByUser_UserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Customer profile not found"));
    }

    // Verify the address belongs to this customer
    private CustomerAddress getOwnedAddress(
            Long customerId,
            Long addressId) {

        return addressRepository
                .findByAddressIdAndCustomer_CustomerId(
                        addressId, customerId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Address not found"));
    }

    // Protect addresses referenced by bookings
    private void checkNotUsedInBooking(Long addressId) {

        if (bookingRepository.existsByAddress_AddressId(addressId)) {
            throw new IllegalStateException(
                    "Address is already used in a booking");
        }
    }

    // Validate customer input
    private void validateRequest(CustomerAddressRequest request) {

        if (request == null
                || request.getAddressLine() == null
                || request.getAddressLine().isBlank()
                || request.getCity() == null
                || request.getCity().isBlank()
                || request.getState() == null
                || request.getState().isBlank()
                || request.getPincode() == null) {

            throw new IllegalArgumentException(
                    "Address line, city, state and PIN code are required");
        }

        String addressLine = request.getAddressLine().trim();
        String city = request.getCity().trim();
        String state = request.getState().trim();
        String pincode = request.getPincode().trim();

        if (addressLine.length() > 255
                || city.length() > 100
                || state.length() > 100
                || (request.getArea() != null
                    && request.getArea().trim().length() > 100)) {

            throw new IllegalArgumentException(
                    "One or more address fields exceed the allowed length");
        }

        if (!pincode.matches("[1-9][0-9]{5}")) {
            throw new IllegalArgumentException(
                    "PIN code must contain 6 valid digits");
        }
    }

    // Copy submitted details into the entity
    private void copyDetails(
            CustomerAddressRequest request,
            CustomerAddress address) {

        address.setAddressLine(request.getAddressLine().trim());

        address.setArea(
                request.getArea() == null
                    ? null
                    : request.getArea().trim());

        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPincode(request.getPincode().trim());
    }

    // Convert entity into response DTO
    private CustomerAddressResponse toResponse(
            CustomerAddress address) {

        return new CustomerAddressResponse(
                address.getAddressId(),
                address.getAddressLine(),
                address.getArea(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getIsDefault()
        );
    }
}