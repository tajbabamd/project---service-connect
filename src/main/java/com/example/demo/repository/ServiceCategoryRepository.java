package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ServiceCategory;

public interface ServiceCategoryRepository
        extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByCategoryName(String categoryName);

    List<ServiceCategory> findByIsActiveTrue();

    boolean existsByCategoryName(String categoryName);
}