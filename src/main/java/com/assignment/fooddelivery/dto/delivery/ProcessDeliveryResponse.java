package com.assignment.fooddelivery.dto.delivery;

import com.assignment.fooddelivery.statemachine.OrderStates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDeliveryResponse {
    private Long orderId;
    private OrderStates orderStatus;
}
