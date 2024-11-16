package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.OrderLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
	public List<OrderLog> findByOrderIdAndIsDeletedFalse(Long orderId);
}
