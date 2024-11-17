package com.assignment.fooddelivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.fooddelivery.model.RestaurantOwner;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {
    // Method to find a restaurant owner by their ID
    Optional<RestaurantOwner> findById(Long id);

    // Custom method to find a restaurant owner by their mobile number, if needed for other validations
    RestaurantOwner findByMobileNumber(String mobileNumber);
}