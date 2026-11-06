package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WorkerSearchResult;
import com.example.demo.entity.Customer;
import com.example.demo.entity.CustomerAddress;
import com.example.demo.repository.CustomerAddressRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ServiceCategoryRepository;
import com.example.demo.repository.WorkerSearchRepository;
import com.example.demo.service.WorkerSearchService;

@Service
public class WorkerSearchServiceImpl implements WorkerSearchService {

	private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Kolkata");

	private final CustomerRepository customerRepository;
	private final CustomerAddressRepository addressRepository;
	private final ServiceCategoryRepository categoryRepository;
	private final WorkerSearchRepository searchRepository;

	public WorkerSearchServiceImpl(CustomerRepository customerRepository, CustomerAddressRepository addressRepository,
			ServiceCategoryRepository categoryRepository, WorkerSearchRepository searchRepository) {

		this.customerRepository = customerRepository;
		this.addressRepository = addressRepository;
		this.categoryRepository = categoryRepository;
		this.searchRepository = searchRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkerSearchResult> search(Long userId, Long addressId, Long categoryId, LocalDateTime start,
			LocalDateTime end) {

		if (addressId == null || addressId <= 0 || categoryId == null || categoryId <= 0) {
			throw new IllegalArgumentException("Address and category IDs must be positive");
		}

		if (start == null || end == null || !start.isBefore(end) || !start.toLocalDate().equals(end.toLocalDate())
				|| !start.isAfter(LocalDateTime.now(SERVICE_ZONE))) {
			throw new IllegalArgumentException("Choose a future start and a later end on the same date");
		}

		Customer customer = customerRepository.findByUser_UserId(userId)
				.orElseThrow(() -> new NoSuchElementException("Customer profile not found"));

		CustomerAddress address = addressRepository
				.findByAddressIdAndCustomer_CustomerId(addressId, customer.getCustomerId())
				.orElseThrow(() -> new NoSuchElementException("Address not found"));

		categoryRepository.findById(categoryId).filter(category -> Boolean.TRUE.equals(category.getIsActive()))
				.orElseThrow(() -> new NoSuchElementException("Active service category not found"));

		return searchRepository.findEligibleWorkers(categoryId, address.getPincode(), start.getDayOfWeek().getValue(),
				start.toLocalTime(), end.toLocalTime(), start.toLocalDate(), start, end);
	}
}
