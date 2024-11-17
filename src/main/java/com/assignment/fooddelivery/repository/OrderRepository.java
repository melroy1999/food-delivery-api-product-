package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	public List<Order> findByRestaurantIdAndIsDeletedFalse(Long restaurantId);
}
