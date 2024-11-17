package com.assignment.fooddelivery.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDeliveryRequest {
    private Long deliveryAgentId;
    private OrderProcessData orderStatus;
}
