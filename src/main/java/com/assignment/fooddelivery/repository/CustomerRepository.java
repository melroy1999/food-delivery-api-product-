package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByMobileNumber(String mobileNumber);
}
