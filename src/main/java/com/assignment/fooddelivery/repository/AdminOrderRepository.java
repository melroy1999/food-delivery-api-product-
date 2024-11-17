package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.AdminOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrderRepository extends JpaRepository<AdminOrder, Long> {
}