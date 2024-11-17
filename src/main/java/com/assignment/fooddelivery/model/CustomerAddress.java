package com.assignment.fooddelivery.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_addresses")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_addresses_customer_id"))
    private Customer customer;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name="is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name="is_archived", nullable = false)
    private boolean isArchived = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and setters
}
