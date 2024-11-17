package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.model.AdminOrder;
import com.assignment.fooddelivery.repository.AdminOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AdminOrderService {

    @Autowired
    private AdminOrderRepository adminOrderRepository;

    public List<AdminOrder> getAllOrders() {
        return adminOrderRepository.findAll();
    }

    public AdminOrder getOrderById(Long id) {
        return adminOrderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public AdminOrder createOrder(AdminOrder order) {
        return adminOrderRepository.save(order);
    }

    public AdminOrder updateOrder(Long id, AdminOrder order) {
        AdminOrder existingOrder = adminOrderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        existingOrder.setStatus(order.getStatus());
        existingOrder.setTotalPrice(order.getTotalPrice());
        // Update other fields as necessary
        return adminOrderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        AdminOrder order = adminOrderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        adminOrderRepository.delete(order);
    }
}