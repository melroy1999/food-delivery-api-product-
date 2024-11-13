package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    @Query("SELECT d FROM DeliveryAgent d WHERE d.mobileNumber = :mobileNo")
    DeliveryAgent findByMobileNumber(String mobileNo);
}
