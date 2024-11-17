package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	public List<Order> findByRestaurantIdAndIsDeletedFalse(Long restaurantId);
	@Query("SELECT o FROM Order o inner join OrderDeliveryAgent oda on o.id = oda.order.id WHERE oda.deliveryAgent.id = :deliveryAgentId AND o.restaurant.id = :restaurantId AND o.orderStatus = 'READY' order by o.id")
	List<Order> getReadyForDeliveryOrders(Long deliveryAgentId, Long restaurantId);
}
