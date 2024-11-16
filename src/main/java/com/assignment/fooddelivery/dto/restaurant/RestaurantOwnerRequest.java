package com.assignment.fooddelivery.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOwnerRequest {
	private String name;
	private String address;
	private String contactNo;
	private String openingDays;
	private String openingTime;
	private String closingTime;
	private boolean dineIn;
	private boolean takeAway;
	private Long ownerId;

}
