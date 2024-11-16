package com.assignment.fooddelivery.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.assignment.fooddelivery.dto.restaurant.MenuItemRequest;
import com.assignment.fooddelivery.dto.restaurant.MenuItemResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderLogResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderResponse;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerRequest;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerResponse;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderLog;
import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.model.RestaurantMenu;
import com.assignment.fooddelivery.repository.OrderLogRepository;
import com.assignment.fooddelivery.repository.OrderRepository;
import com.assignment.fooddelivery.repository.RestaurantMenuRepository;
import com.assignment.fooddelivery.repository.RestaurantOwnerRepository;
import com.assignment.fooddelivery.repository.RestaurantRepository;
import com.assignment.fooddelivery.statemachine.OrderStates;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestaurantService {
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private RestaurantOwnerRepository restaurantOwnerRepository;
	@Autowired
	private RestaurantMenuRepository restaurantMenuRepository;
	@Autowired
	private OrderLogRepository orderLogRepository;
	@Autowired
	private OrderRepository orderRepository;

	public Restaurant getRestaurantById(Long restaurantId) {
		// Fetch restaurant details from database
		return restaurantRepository.findById(restaurantId).orElse(null);
	}

	public List<Restaurant> getAllRestaurants() {
		return restaurantRepository.findAll();
	}

	public RestaurantOwnerResponse registerOwner(RestaurantOwnerRequest restaurantRequest) {
		try {
			// Check if a restaurant with the given contact number already exists
			Restaurant existingRestaurant = restaurantRepository.findByContactNo(restaurantRequest.getContactNo());
			if (existingRestaurant != null) {
				log.error("Restaurant with contact number {} already exists", restaurantRequest.getContactNo());
				throw new ServiceException(HttpStatus.BAD_REQUEST,
						"Restaurant with contact number " + restaurantRequest.getContactNo() + " already exists");
			}

			// Create a new restaurant
			Restaurant newRestaurant = Restaurant.builder().name(restaurantRequest.getName())
					.address(restaurantRequest.getAddress()).contactNo(restaurantRequest.getContactNo())
					.openingDays(restaurantRequest.getOpeningDays()).openingTime(restaurantRequest.getOpeningTime())
					.closingTime(restaurantRequest.getClosingTime()).dineIn(restaurantRequest.isDineIn())
					.takeAway(restaurantRequest.isTakeAway()).isDeleted(false).isArchived(false)
					.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

			// Save the new restaurant to the database
			restaurantRepository.save(newRestaurant);

			log.info("Restaurant added successfully with contact number: {}", restaurantRequest.getContactNo());

			return RestaurantOwnerResponse.builder().restaurantId(newRestaurant.getId()).name(newRestaurant.getName())
					.address(newRestaurant.getAddress()).contactNo(newRestaurant.getContactNo()).status("ACTIVE")
					.dineIn(newRestaurant.isDineIn()).takeAway(newRestaurant.isTakeAway()).build();

		} catch (ServiceException e) {
			log.error("Error while registering restaurant: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while registering restaurant: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public List<MenuItemResponse> addMenuToRestaurant(Long restaurantId, List<MenuItemRequest> menuItems) {
		// Find the restaurant by ID
		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
				() -> new ServiceException(HttpStatus.NOT_FOUND, "Restaurant with ID " + restaurantId + " not found"));

		List<RestaurantMenu> restaurantMenuItems = menuItems.stream().map(menuItemRequest -> {
			return RestaurantMenu.builder().restaurant(restaurant).itemName(menuItemRequest.getItemName())
					.itemDescription(menuItemRequest.getItemDescription()).itemPrice(menuItemRequest.getItemPrice())
					.isAvailable(menuItemRequest.isAvailable()).isDeleted(false).isArchived(false)
					.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
		}).collect(Collectors.toList());

		// Save the list of RestaurantMenu items
		restaurantMenuRepository.saveAll(restaurantMenuItems);

		List<MenuItemResponse> response = restaurantMenuItems.stream().map(menuItem -> {

			return MenuItemResponse.builder().id(menuItem.getId()).itemName(menuItem.getItemName())
					.itemDescription(menuItem.getItemDescription()).itemPrice(menuItem.getItemPrice())
					.isAvailable(menuItem.isAvailable()).build();
		}).collect(Collectors.toList());

		return response;
	}

	public RestaurantOwnerResponse updateRestaurant(Long restaurantId, RestaurantOwnerRequest restaurantRequest) {
		try {
			// Check if the restaurant exists
			Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
					.orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Restaurant not found"));

			// Check if another restaurant with the same contact number exists (but not the
			// current one)
			Restaurant duplicateRestaurant = restaurantRepository
					.findByContactNoAndIdNot(restaurantRequest.getContactNo(), restaurantId);
			if (duplicateRestaurant != null) {
				log.error("Restaurant with contact number {} already exists", restaurantRequest.getContactNo());
				throw new ServiceException(HttpStatus.BAD_REQUEST,
						"Restaurant with contact number " + restaurantRequest.getContactNo() + " already exists");
			}

			// Update the restaurant details
			existingRestaurant.setName(restaurantRequest.getName());
			existingRestaurant.setAddress(restaurantRequest.getAddress());
			existingRestaurant.setContactNo(restaurantRequest.getContactNo());
			existingRestaurant.setOpeningDays(restaurantRequest.getOpeningDays());
			existingRestaurant.setOpeningTime(restaurantRequest.getOpeningTime());
			existingRestaurant.setClosingTime(restaurantRequest.getClosingTime());
			existingRestaurant.setDineIn(restaurantRequest.isDineIn());
			existingRestaurant.setTakeAway(restaurantRequest.isTakeAway());
			existingRestaurant.setUpdatedAt(LocalDateTime.now());

			// Save the updated restaurant to the database
			restaurantRepository.save(existingRestaurant);

			log.info("Restaurant updated successfully with ID: {}", restaurantId);

			return RestaurantOwnerResponse.builder().restaurantId(existingRestaurant.getId())
					.name(existingRestaurant.getName()).address(existingRestaurant.getAddress())
					.contactNo(existingRestaurant.getContactNo()).status("ACTIVE").dineIn(existingRestaurant.isDineIn())
					.takeAway(existingRestaurant.isTakeAway()).build();

		} catch (ServiceException e) {
			log.error("Error while updating restaurant: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while updating restaurant: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public List<MenuItemResponse> updateMenuItem(Long restaurantId, List<MenuItemRequest> menuItems) {
		try {

			Restaurant restaurant = restaurantRepository.findById(restaurantId)
					.orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND,
							"Restaurant with ID " + restaurantId + " not found"));

			List<RestaurantMenu> updatedMenuItems = new ArrayList<>();

			for (MenuItemRequest menuItemRequest : menuItems) {

				RestaurantMenu existingMenuItem = restaurantMenuRepository.findById(menuItemRequest.getId())
						.orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND,
								"Menu item with ID " + menuItemRequest.getId() + " not found"));

				if (!existingMenuItem.getRestaurant().getId().equals(restaurantId)) {
					throw new ServiceException(HttpStatus.BAD_REQUEST,
							"Menu item does not belong to the specified restaurant");
				}

				existingMenuItem.setItemName(menuItemRequest.getItemName());
				existingMenuItem.setItemDescription(menuItemRequest.getItemDescription());
				existingMenuItem.setItemPrice(menuItemRequest.getItemPrice());
				existingMenuItem.setAvailable(menuItemRequest.isAvailable());
				existingMenuItem.setUpdatedAt(LocalDateTime.now());

				updatedMenuItems.add(existingMenuItem);
			}

			restaurantMenuRepository.saveAll(updatedMenuItems);

			List<MenuItemResponse> response = updatedMenuItems.stream().map(menuItem -> {
				return MenuItemResponse.builder().id(menuItem.getId()).itemName(menuItem.getItemName())
						.itemDescription(menuItem.getItemDescription()).itemPrice(menuItem.getItemPrice())
						.isAvailable(menuItem.isAvailable()).build();
			}).collect(Collectors.toList());

			return response;

		} catch (ServiceException e) {
			log.error("Error while updating menu items: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while updating menu items: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public List<OrderResponse> getOrderByRestaurantId(Long restaurantId) {
		try {

			List<Order> orders = orderRepository.findByRestaurantIdAndIsDeletedFalse(restaurantId);

			if (orders.isEmpty()) {
				log.error("No orders found for restaurant with ID {}", restaurantId);
				throw new ServiceException(HttpStatus.NOT_FOUND, "No orders found for this restaurant");
			}

			List<OrderResponse> orderResponses = orders.stream().map(order -> {

				List<OrderLog> orderLogs = orderLogRepository.findByOrderIdAndIsDeletedFalse(order.getId());

				List<OrderLogResponse> orderLogResponses = orderLogs.stream().map(orderLog -> {
					return OrderLogResponse.builder().logId(orderLog.getId())
							.orderSubStatus(orderLog.getOrderSubStatus()).remarks(orderLog.getRemarks())
							.enteredBy(orderLog.getEnteredBy()).enteredById(orderLog.getEnteredById())
							.createdAt(orderLog.getCreatedAt()).build();
				}).collect(Collectors.toList());

				return OrderResponse.builder().orderId(order.getId()).customerId(order.getCustomer().getId())

						.customerName(order.getCustomer().getName()).orderStatus(order.getOrderStatus().toString())
						.orderDetails(order.getOrderDetails()).totalAmount(order.getTotalAmount())
						.createdAt(order.getCreatedAt()).updatedAt(order.getUpdatedAt()).orderLogs(orderLogResponses)
						.build();
			}).collect(Collectors.toList());

			return orderResponses;
		} catch (ServiceException e) {
			log.error("Error while fetching orders for restaurant: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while fetching orders for restaurant: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public OrderResponse updateOrderStatus(Long orderId, OrderStates newStatus) {
		try {

			Order order = orderRepository.findById(orderId).orElseThrow(
					() -> new ServiceException(HttpStatus.NOT_FOUND, "Order with ID " + orderId + " not found"));

			order.setOrderStatus(newStatus);
			order.setUpdatedAt(LocalDateTime.now());
			orderRepository.save(order);

			OrderLog orderLog = OrderLog.builder().order(order).orderSubStatus("STATUS_UPDATED")
					.remarks("Order status updated to " + newStatus).enteredBy("SYSTEM").enteredById(0L)
					.isDeleted(false).isArchived(false).createdAt(LocalDateTime.now()).build();
			orderLogRepository.save(orderLog);

			return OrderResponse.builder().orderId(order.getId()).customerId(order.getCustomer().getId())
					.customerName(order.getCustomer().getName()).orderStatus(order.getOrderStatus().toString())
					.orderDetails(order.getOrderDetails()).totalAmount(order.getTotalAmount())
					.createdAt(order.getCreatedAt()).updatedAt(order.getUpdatedAt()) // Updated timestamp
					.build();
		} catch (ServiceException e) {
			log.error("Error while updating order status: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while updating order status: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}
}
