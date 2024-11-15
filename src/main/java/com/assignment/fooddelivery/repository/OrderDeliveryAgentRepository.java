package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.OrderDeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDeliveryAgentRepository extends JpaRepository<OrderDeliveryAgent, Long> {
    OrderDeliveryAgent findByDeliveryAgent(DeliveryAgent deliveryAgent);
}
