package com.assignment.fooddelivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 15)
    private String contactNo;

    @Column(nullable = false, length = 100)
    private String openingDays;

    @Column(nullable = false)
    private String openingTime;

    @Column(nullable = false)
    private String closingTime;

    @Column(nullable = false)
    private boolean dineIn = false;

    @Column(nullable = false)
    private boolean takeAway = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_restaurants_owner_id"))
    private RestaurantOwner owner;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isArchived = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
