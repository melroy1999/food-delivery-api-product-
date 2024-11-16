package com.assignment.fooddelivery.model;

import com.assignment.fooddelivery.statemachine.OrderStates;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
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
    @Column(name="order_status", nullable = false)
    private OrderStates orderStatus;

    @Column(name="order_details", nullable = false, columnDefinition = "json")
    private String orderDetails; // Using String to represent JSON data

    @Column(name="total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name="is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name="is_archived", nullable = false)
    private boolean isArchived = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and setters
    public OrderStates getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStates orderStatus) {
        this.orderStatus = orderStatus;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

