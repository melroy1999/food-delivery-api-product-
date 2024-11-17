package com.assignment.fooddelivery.controller;

import java.util.List;
import java.util.Map;

import com.assignment.fooddelivery.dto.restaurant.*;
import com.assignment.fooddelivery.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.service.RestaurantService;
import com.assignment.fooddelivery.statemachine.OrderStates;

//import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
	@Autowired
	RestaurantService restaurantService;

	// Endpoint to register a new restaurant
	@PostMapping("/owner/register")
	public ApiResponse<RestaurantOwnerResponse> registerOwner(@RequestBody RestaurantOwnerRequest ownerRequest) {
		try {
			RestaurantOwnerResponse response = restaurantService.registerRestaurantOwner(ownerRequest);
			return ApiResponse.<RestaurantOwnerResponse>builder().status("success").data(response).build();
		}catch (ServiceException e) {
			return ApiResponse.<RestaurantOwnerResponse>builder().status("failed")
					.error(ErrorMessage.builder().error(e.getMessage()).description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			return ApiResponse
					.<RestaurantOwnerResponse>builder().status("failed").error(ErrorMessage.builder()
							.error("Error while registering restaurant owner").description(e.getMessage()).build())
					.build();
		}
	}

	@PostMapping("/register")
	public ApiResponse<RestaurantResponse> registerRestaurant(@RequestBody RestaurantRequest ownerRequest) {
		try {
			RestaurantResponse response = restaurantService.registerRestaurant(ownerRequest);
			return ApiResponse.<RestaurantResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse
					.<RestaurantResponse>builder().error(ErrorMessage.builder()
							.error("Error while registering restaurant owner").description(e.getMessage()).build())
					.build();
		}
	}

	// Endpoint to add menu items to an existing restaurant
	@PostMapping("/{restaurantId}/menu")
	public ApiResponse<List<MenuItemResponse>> addMenuToRestaurant(@PathVariable Long restaurantId,
			@RequestBody List<MenuItemRequest> menuItems) {
		try {
			List<MenuItemResponse> response = restaurantService.addMenuToRestaurant(restaurantId, menuItems);
			return ApiResponse.<List<MenuItemResponse>>builder().status("success").data(response).build();
		}catch (ServiceException e) {
			return ApiResponse.<List<MenuItemResponse>>builder().status("failed")
					.error(ErrorMessage.builder().error("Menu item addition failed").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			return ApiResponse.<List<MenuItemResponse>>builder().status("failed")
					.error(ErrorMessage.builder().error("Menu item addition failed").description(e.getMessage()).build())
					.build();
		}
	}

	@PutMapping("/{restaurantId}/update-restaurant")
	public ApiResponse<RestaurantResponse> updateRestaurant(@PathVariable Long restaurantId,
															@RequestBody RestaurantRequest updatedRestaurant) {
		try {
			RestaurantResponse response = restaurantService.updateRestaurant(restaurantId, updatedRestaurant);
			return ApiResponse.<RestaurantResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<RestaurantResponse>builder()
					.error(ErrorMessage.builder().error("Restaurant update failed").description(e.getMessage()).build())
					.build();
		}
	}

	@PutMapping("/menu/{itemId}")
	public ApiResponse<MenuItemResponse> updateMenuItem(@PathVariable Long itemId,
			@RequestBody MenuItemRequest restaurantMenu) {
		try {
			MenuItemResponse response = restaurantService.updateMenuItem(itemId, restaurantMenu);
			return ApiResponse.<MenuItemResponse>builder().status("success").data(response).build();
		} catch (Exception e) {
			return ApiResponse.<MenuItemResponse>builder()
					.error(ErrorMessage.builder().error("Menu item update failed").description(e.getMessage()).build())
					.build();
		}
	}

	@GetMapping("/{restaurantId}/orders")
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
	public ApiResponse<OrderStatusUpdateResponse> updateOrderStatus(@PathVariable Long orderId,
			@RequestBody OrderStatusUpdateRequest requestPayload) {
		try {

			OrderStatusUpdateResponse response = restaurantService.updateOrderStatus(orderId, requestPayload);

			return ApiResponse.<OrderStatusUpdateResponse>builder().status("success").data(response).build();
		} catch (ServiceException e) {
			return ApiResponse.<OrderStatusUpdateResponse>builder().status("failed")
					.error(ErrorMessage.builder().error("Order status update failed").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			return ApiResponse.<OrderStatusUpdateResponse>builder()
					.error(ErrorMessage.builder().error("Restaurant Id not found").description(e.getMessage()).build())
					.build();
		}
	}
}