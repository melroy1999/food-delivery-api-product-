package com.assignment.fooddelivery.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.restaurant.MenuItemRequest;
import com.assignment.fooddelivery.dto.restaurant.MenuItemResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderResponse;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerRequest;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerResponse;
import com.assignment.fooddelivery.service.RestaurantService;
import com.assignment.fooddelivery.statemachine.OrderStates;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class RestaurantController {
	@Autowired
	RestaurantService restaurantService;

	// Endpoint to register a new restaurant
	@PostMapping("/restaurant-owner/register")
	@Operation(summary = "Register a restaurant owner", 
    description = "Registering Restaurant owner",
    responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Restaurant with the contact no already exists")
    })
	public ApiResponse<RestaurantOwnerResponse> registerOwner(@RequestBody RestaurantOwnerRequest ownerRequest) {
		try {
			RestaurantOwnerResponse response = restaurantService.registerOwner(ownerRequest);
			return ApiResponse.<RestaurantOwnerResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse
					.<RestaurantOwnerResponse>builder().error(ErrorMessage.builder()
							.error("Error while registering restaurant owner").description(e.getMessage()).build())
					.build();
		}
	}

	// Endpoint to add menu items to an existing restaurant
	@PostMapping("/restaurant/{restaurantId}/menu")
	public ApiResponse<List<MenuItemResponse>> addMenuToRestaurant(@PathVariable Long restaurantId,
			@RequestBody List<MenuItemRequest> menuItems) {
		try {
			List<MenuItemResponse> response = restaurantService.addMenuToRestaurant(restaurantId, menuItems);
			return ApiResponse.<List<MenuItemResponse>>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<List<MenuItemResponse>>builder()
					.error(ErrorMessage.builder().error("Restaurant Id not found").description(e.getMessage()).build())
					.build();
		}
	}

	@PutMapping("/update-restaurant/{restaurantId}")
	public ApiResponse<RestaurantOwnerResponse> updateRestaurant(@PathVariable Long restaurantId,
			@RequestBody RestaurantOwnerRequest updatedRestaurant) {
		try {
			RestaurantOwnerResponse response = restaurantService.updateRestaurant(restaurantId, updatedRestaurant);
			return ApiResponse.<RestaurantOwnerResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<RestaurantOwnerResponse>builder()
					.error(ErrorMessage.builder().error("Restaurant update failed").description(e.getMessage()).build())
					.build();
		}
	}

	@PutMapping("/menu/{itemId}")
	public ApiResponse<List<MenuItemResponse>> updateMenuItem(@PathVariable Long itemId,
			@RequestBody List<MenuItemRequest> restaurantMenu) {
		try {
			List<MenuItemResponse> response = restaurantService.updateMenuItem(itemId, restaurantMenu);
			return ApiResponse.<List<MenuItemResponse>>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<List<MenuItemResponse>>builder()
					.error(ErrorMessage.builder().error("Menu item update failed").description(e.getMessage()).build())
					.build();
		}
	}

	@GetMapping("/restaurant/{restaurantId}")
	public ApiResponse<List<OrderResponse>> getOrdersByRestaurant(@PathVariable Long restaurantId) {
		try {
			List<OrderResponse> orders = restaurantService.getOrderByRestaurantId(restaurantId);
			return ApiResponse.<List<OrderResponse>>builder().status("success").data(orders).build();
		} catch (Exception e) {
			return ApiResponse.<List<OrderResponse>>builder()
					.error(ErrorMessage.builder().error("Orders retrieval failed").description(e.getMessage()).build())
					.build();
		}
	}

	@PostMapping("/order/{orderId}/status")
	public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
			@RequestBody Map<String, String> requestPayload) {
		 OrderStates newStatus = OrderStates.valueOf(requestPayload.get("newStatus"));
		try {

			OrderResponse response = restaurantService.updateOrderStatus(orderId, newStatus);

			return ApiResponse.<OrderResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<OrderResponse>builder()
					.error(ErrorMessage.builder().error("Restaurant Id not found").description(e.getMessage()).build())
					.build();
		}
	}
}