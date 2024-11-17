package com.assignment.fooddelivery.controller;

import java.util.List;
import java.util.Optional;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.customer.CustomerRegistrationDto;
import com.assignment.fooddelivery.dto.customer.CustomerResponse;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentResponse;
import com.assignment.fooddelivery.dto.order.OrderLogResponse;
import com.assignment.fooddelivery.dto.order.OrderRequest;
import com.assignment.fooddelivery.dto.order.OrderResponse;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.RestaurantMenu;
import com.assignment.fooddelivery.service.CustomerService;
import com.assignment.fooddelivery.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.service.RestaurantService;

@Slf4j
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private OrderService orderService;

	// Register Customer
	@PostMapping("/register")
	public ApiResponse<CustomerResponse> registerCustomer(@RequestBody CustomerRegistrationDto registrationDto) {
		try {
			CustomerResponse response = customerService.registerCustomer(registrationDto);
			return ApiResponse.<CustomerResponse>builder().status("success").data(response).build();
		} catch (ServiceException e) {
			log.error("ServiceException occured while adding customer with mobile number: {}", registrationDto.getMobileNumber(), e);
			return ApiResponse.<CustomerResponse>builder().status("failed")
					.error(ErrorMessage.builder().error("Error while create delivery agent master").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			log.error("Exception occured while adding customer with mobile number: {}", registrationDto.getMobileNumber(), e);
			return ApiResponse.<CustomerResponse>builder().status("failed").error(
					ErrorMessage.builder().error("Error while create customer master").description(e.getMessage()).build()
			).build();
		}
	}

	// Browse Restaurants
	@GetMapping("/restaurants")
	public ResponseEntity<List<Restaurant>> getResturant(){
		List<Restaurant> restaurants = restaurantService.getAllRestaurants();
		if(restaurants.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(restaurants, HttpStatus.OK);
	}

	// Search Menus
	@GetMapping("/menus/search")
	public ResponseEntity<List<RestaurantMenu>> searchMenus(@RequestParam String query, @RequestParam Optional<String> type) {
		return ResponseEntity.ok(restaurantService.searchMenus(query, type));
	}

	// Place an Order
	@PostMapping("/place-order")
	public ApiResponse<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
		try{
			OrderResponse orderResponse = orderService.createOrder(orderRequest);
			return ApiResponse.<OrderResponse>builder().status("success").data(orderResponse).build();
		}catch (ServiceException e) {
			log.error("ServiceException occured while placing order for customer with id : {}", orderRequest.getCustomerId(), e);
			return ApiResponse.<OrderResponse>builder().status("failed")
					.error(ErrorMessage.builder().error("Error while placing order").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			log.error("Exception occured while placing order for customer with id : {}", orderRequest.getCustomerId(), e);
			return ApiResponse.<OrderResponse>builder().status("failed").error(
					ErrorMessage.builder().error("Error while placing order").description(e.getMessage()).build()
			).build();
		}
	}

	// Track Order
	@GetMapping("/orders/{orderId}/status")
	public ApiResponse<Order> trackOrder(@PathVariable Long orderId) {
		try {
			return ApiResponse.<Order>builder().status("success").data(orderService.getOrderByOrderId(orderId)).build();
		}catch (ServiceException e) {
			log.error("ServiceException occured while getting order with id: {}", orderId, e);
			return ApiResponse.<Order>builder().status("failed")
					.error(ErrorMessage.builder().error("Error while getting order").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			log.error("Exception occured while getting order with id: {}", orderId, e);
			return ApiResponse.<Order>builder().status("failed").error(
					ErrorMessage.builder().error("Error while getting order").description(e.getMessage()).build()
			).build();
		}
	}

	// View Order History
	@GetMapping("/order/{orderId}/history")
	public ApiResponse<List<OrderLogResponse>> getOrderHistory(@PathVariable("orderId") Long orderId) {
		try{
			return ApiResponse.<List<OrderLogResponse>>builder().status("success").data(orderService.getOrderLogs(orderId)).build();
		}catch (ServiceException e) {
			log.error("ServiceException occured while getting order logs with id: {}", orderId, e);
			return ApiResponse.<List<OrderLogResponse>>builder().status("failed")
					.error(ErrorMessage.builder().error("Error while getting order logs").description(e.getMessage()).build())
					.build();
		}
		catch (Exception e) {
			log.error("Exception occured while getting order logs with id: {}", orderId, e);
			return ApiResponse.<List<OrderLogResponse>>builder().status("failed").error(
					ErrorMessage.builder().error("Error while getting order logs").description(e.getMessage()).build()
			).build();
		}
	}
}
