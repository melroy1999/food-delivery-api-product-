package com.assignment.fooddelivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_agents")
public class DeliveryAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String mobileNumber;

    @Column(nullable = false, length = 100)
    private String vehicleType;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_delivery_agents_restaurant_id"))
    private Restaurant restaurant;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isArchived = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
