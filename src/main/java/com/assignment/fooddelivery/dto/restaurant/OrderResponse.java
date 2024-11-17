package com.assignment.fooddelivery.dto.restaurant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	private Long orderId;
	private Long customerId;
	private String customerName;
	private String orderStatus;
	private String orderDetails;
	private BigDecimal totalAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt; 
	private List<OrderLogResponse> orderLogs;
}
