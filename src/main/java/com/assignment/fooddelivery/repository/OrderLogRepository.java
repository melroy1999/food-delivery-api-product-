package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
}
