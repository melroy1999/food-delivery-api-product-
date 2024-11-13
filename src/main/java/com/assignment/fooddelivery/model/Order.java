package com.assignment.fooddelivery.model;

import com.assignment.fooddelivery.enums.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_orders_customer_id"))
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_orders_restaurant_id"))
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false, columnDefinition = "json")
    private String orderDetails; // Using String to represent JSON data

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isArchived = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and setters
}

