package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStateRepository extends JpaRepository<OrderState, Long> {
    Optional<OrderState> findByOrderId(Long orderId);
}
