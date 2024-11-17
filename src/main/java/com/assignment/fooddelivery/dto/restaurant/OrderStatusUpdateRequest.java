package com.assignment.fooddelivery.dto.restaurant;

import com.assignment.fooddelivery.dto.delivery.OrderProcessData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    private Long restaurantOwnerId;
    private OrderProcessData orderStatus;
}
