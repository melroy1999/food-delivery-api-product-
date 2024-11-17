package com.assignment.fooddelivery.dto.delivery;

import com.assignment.fooddelivery.dto.order.OrderMenuDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadyToDeliverOrder {
    private Long orderId;
    private String orderAddress;
    private String customerName;
    private String customerContactNo;
    private List<OrderMenuDetails> orderMenuDetails;
    private BigDecimal orderTotalPrice;
}
