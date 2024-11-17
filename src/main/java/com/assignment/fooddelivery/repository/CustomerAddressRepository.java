package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.Customer;
import com.assignment.fooddelivery.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    CustomerAddress findByCustomer(Customer customer);
}
