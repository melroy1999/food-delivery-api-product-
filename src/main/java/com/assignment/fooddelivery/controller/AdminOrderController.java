package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.model.AdminOrder;
import com.assignment.fooddelivery.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<List<AdminOrder>> getAllOrders() {
        List<AdminOrder> orders = adminOrderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrder> getOrderById(@PathVariable Long id) {
        AdminOrder order = adminOrderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AdminOrder> createOrder(@RequestBody AdminOrder order) {
        AdminOrder createdOrder = adminOrderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminOrder> updateOrder(@PathVariable Long id, @RequestBody AdminOrder order) {
        AdminOrder updatedOrder = adminOrderService.updateOrder(id, order);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        adminOrderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}