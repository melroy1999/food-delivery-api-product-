package com.assignment.fooddelivery.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
@Entity
@Table(name = "order_delivery_agents")
public class OrderDeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_delivery_agents_order_id"))
    private Order order;

    @ManyToOne
    @JoinColumn(name = "delivery_agent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_delivery_agents_delivery_agent_id"))
    private DeliveryAgent deliveryAgent;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Getters and setters omitted for brevity
    public void setDeliveryAgentId(Long deliveryAgentId) {
        this.deliveryAgent.setId(deliveryAgentId);
    }
}
