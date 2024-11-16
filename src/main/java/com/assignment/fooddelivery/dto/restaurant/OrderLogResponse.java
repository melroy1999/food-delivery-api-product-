package com.assignment.fooddelivery.dto.restaurant;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLogResponse {

	private Long logId;
	private String orderSubStatus;
	private String remarks;
	private String enteredBy;
	private Long enteredById;
	private LocalDateTime createdAt;
}