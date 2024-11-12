package com.assignment.fooddelivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_types")
public class RestaurantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_restaurant_types_restaurant_id"))
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String cuisine;

    @Column(nullable = false)
    private boolean veg = false;

    @Column(nullable = false)
    private boolean nonVeg = false;

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

