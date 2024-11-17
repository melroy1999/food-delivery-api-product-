package com.assignment.fooddelivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.assignment.fooddelivery.dto.delivery.ProcessDeliveryResponse;
import com.assignment.fooddelivery.dto.restaurant.*;
import com.assignment.fooddelivery.model.*;
import com.assignment.fooddelivery.statemachine.OrderEvents;
import com.assignment.fooddelivery.statemachine.OrderStates;
import com.assignment.fooddelivery.statemachine.ProcessOrderEvent;
import com.assignment.fooddelivery.utils.CommonOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.assignment.fooddelivery.dto.restaurant.MenuItemRequest;
import com.assignment.fooddelivery.dto.restaurant.MenuItemResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderLogResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderResponse;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerRequest;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerResponse;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderLog;
import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.model.RestaurantMenu;
import com.assignment.fooddelivery.model.RestaurantOwner;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.repository.OrderLogRepository;
import com.assignment.fooddelivery.repository.OrderRepository;
import com.assignment.fooddelivery.repository.RestaurantMenuRepository;
import com.assignment.fooddelivery.repository.RestaurantOwnerRepository;
import com.assignment.fooddelivery.repository.RestaurantRepository;
import com.assignment.fooddelivery.enums.UserTypes;

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
	@Autowired
	private LoginService loginService;
	@Autowired
	private CommonOperations commonOperations;
	@Autowired
	private ProcessOrderEvent processOrderEvent;
	@Autowired
	private OrderService orderService;


	public Restaurant getRestaurantById(Long restaurantId) {
		// Fetch restaurant details from database
		return restaurantRepository.findById(restaurantId).orElse(null);
	}

	public List<Restaurant> getAllRestaurants() {
		return restaurantRepository.findAll();
	}

	public RestaurantResponse registerRestaurant(RestaurantRequest restaurantRequest) {
		try {
			RestaurantOwner owner;

			// Check if an ownerId is provided in the request
			if (restaurantRequest.getOwnerId() != null) {
				// Look for an existing owner
				owner = restaurantOwnerRepository.findById(restaurantRequest.getOwnerId())
						.orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Owner not found"));
			} else {
				// No ownerId provided, so create a new owner
				owner = RestaurantOwner.builder().name(restaurantRequest.getName())
						.mobileNumber(restaurantRequest.getContactNo()).email(restaurantRequest.getEmail())
						.isDeleted(false).isArchived(false).createdAt(LocalDateTime.now())
						.updatedAt(LocalDateTime.now()).build();

				// Save the new owner to the database
				owner = restaurantOwnerRepository.save(owner);
			}

			// Proceed to create a new restaurant
			Restaurant newRestaurant = Restaurant.builder().name(restaurantRequest.getName())
					.address(restaurantRequest.getAddress()).contactNo(restaurantRequest.getContactNo())
					.openingDays(restaurantRequest.getOpeningDays()).openingTime(restaurantRequest.getOpeningTime())
					.closingTime(restaurantRequest.getClosingTime()).dineIn(restaurantRequest.isDineIn())
					.takeAway(restaurantRequest.isTakeAway()).owner(owner) // Assign the owner to the restaurant
					.isDeleted(false).isArchived(false).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
					.build();

			// Save the new restaurant to the database
			restaurantRepository.save(newRestaurant);
			loginService.addLoginDetails(restaurantRequest.getContactNo(), restaurantRequest.getPassword(), UserTypes.RESTAURANT.name());
			log.info("Restaurant added successfully with contact number: {}", restaurantRequest.getContactNo());

			return RestaurantResponse.builder().restaurantId(newRestaurant.getId()).name(newRestaurant.getName())
					.address(newRestaurant.getAddress()).contactNo(newRestaurant.getContactNo()).status("ACTIVE")
					.dineIn(newRestaurant.getDineIn()).takeAway(newRestaurant.getTakeAway()).build();

		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public List<MenuItemResponse> addMenuToRestaurant(Long restaurantId, List<MenuItemRequest> menuItems) {
		// Find the restaurant by ID
		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
				() -> new ServiceException(HttpStatus.NOT_FOUND, "Restaurant with ID " + restaurantId + " not found"));

		List<RestaurantMenu> restaurantMenuItems = menuItems.stream().map(menuItemRequest -> RestaurantMenu.builder().restaurant(restaurant).itemName(menuItemRequest.getItemName())
                .itemDescription(menuItemRequest.getItemDescription()).itemPrice(menuItemRequest.getItemPrice())
                .isAvailable(menuItemRequest.getIsAvailable()).isDeleted(false).isArchived(false)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()).collect(Collectors.toList());

		// Save the list of RestaurantMenu items
		restaurantMenuRepository.saveAll(restaurantMenuItems);

		List<MenuItemResponse> response = restaurantMenuItems.stream().map(menuItem -> {

			return MenuItemResponse.builder().id(menuItem.getId()).itemName(menuItem.getItemName())
					.itemDescription(menuItem.getItemDescription()).itemPrice(menuItem.getItemPrice())
					.isAvailable(menuItem.getIsAvailable()).build();
		}).collect(Collectors.toList());

		return response;
	}

	public RestaurantResponse updateRestaurant(Long restaurantId, RestaurantRequest restaurantRequest) {
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
			existingRestaurant.setOpeningDays(restaurantRequest.getOpeningDays());
			existingRestaurant.setOpeningTime(restaurantRequest.getOpeningTime());
			existingRestaurant.setClosingTime(restaurantRequest.getClosingTime());
			existingRestaurant.setDineIn(restaurantRequest.isDineIn());
			existingRestaurant.setTakeAway(restaurantRequest.isTakeAway());
			existingRestaurant.setUpdatedAt(LocalDateTime.now());

			// Save the updated restaurant to the database
			restaurantRepository.save(existingRestaurant);

			log.info("Restaurant updated successfully with ID: {}", restaurantId);

			return RestaurantResponse.builder().restaurantId(existingRestaurant.getId())
					.name(existingRestaurant.getName()).address(existingRestaurant.getAddress())
					.contactNo(existingRestaurant.getContactNo()).status("ACTIVE").dineIn(existingRestaurant.getDineIn())
					.takeAway(existingRestaurant.getTakeAway()).build();

		} catch (ServiceException e) {
			log.error("Error while updating restaurant: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while updating restaurant: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	public MenuItemResponse updateMenuItem(Long itemId, MenuItemRequest menuItems) {
		try {
				RestaurantMenu existingMenuItem = restaurantMenuRepository.findById(itemId)
						.orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND,
								"Menu item with ID " + itemId + " not found"));

				existingMenuItem.setItemName(menuItems.getItemName());
				existingMenuItem.setItemDescription(menuItems.getItemDescription());
				existingMenuItem.setItemPrice(menuItems.getItemPrice());
				existingMenuItem.setIsAvailable(menuItems.getIsAvailable());
				existingMenuItem.setUpdatedAt(LocalDateTime.now());

			restaurantMenuRepository.save(existingMenuItem);

			return MenuItemResponse.builder().id(existingMenuItem.getId()).itemName(existingMenuItem.getItemName())
						.itemDescription(existingMenuItem.getItemDescription()).itemPrice(existingMenuItem.getItemPrice())
						.isAvailable(existingMenuItem.getIsAvailable()).build();

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

	public OrderStatusUpdateResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest requestPayload) {
		try {

			Order order = orderRepository.findById(orderId).orElseThrow(
					() -> new ServiceException(HttpStatus.NOT_FOUND, "Order with ID " + orderId + " not found"));

			if(!commonOperations.isEligibleOrderEvent(UserTypes.RESTAURANT_OWNER.name(), OrderEvents.valueOf(requestPayload.getOrderStatus().getOrderEvent()))) {
				log.error("Restaurant owner is not eligible to perform this action");
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant owner is not eligible to perform this action");
			}

			RestaurantOwner restaurantOwner = restaurantOwnerRepository.findById(requestPayload.getRestaurantOwnerId()).orElse(null);
			if (restaurantOwner == null) {
				log.error("Restaurant owner with id {} not found", requestPayload.getRestaurantOwnerId());
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant owner with id " + requestPayload.getRestaurantOwnerId() + " not found");
			}
			if(!isValidRestaurantOwner(restaurantOwner.getId(), order.getRestaurant().getId())) {
				log.error("Restaurant owner with id {} is not the owner of the restaurant with id {}", restaurantOwner.getId(), order.getRestaurant().getId());
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant owner with id " + restaurantOwner.getId() + " is not the owner of the restaurant with id " + order.getRestaurant().getId());
			}
			if(!commonOperations.isOrderEventValid(requestPayload.getOrderStatus().getOrderEvent())) {
				log.error("Invalid order status: {}", requestPayload.getOrderStatus().getOrderEvent());
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid order status: " + requestPayload.getOrderStatus().getOrderEvent());
			}
			if (order.getOrderStatus() == OrderStates.DELIVERED || order.getOrderStatus() == OrderStates.CANCELLED) {
				log.error("Order with id {} is {}", orderId, order.getOrderStatus());
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Order with id " + orderId + " is " + order.getOrderStatus());
			}
			OrderEvents orderEvent = OrderEvents.valueOf(requestPayload.getOrderStatus().getOrderEvent());
			OrderStates orderStates = processOrderEvent.process(order.getId(), orderEvent, requestPayload.getOrderStatus().getComment(), UserTypes.RESTAURANT_OWNER, restaurantOwner.getId());
			if(order.getOrderStatus().ordinal() >= orderStates.ordinal()) {
				log.error("Order state transition failed for order with id: {}", orderId);
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Order state transition failed for order with id: " + orderId);
			}
			order = orderService.getOrderByOrderId(orderId);
			log.info("Order states changed successfully by owner: {}", restaurantOwner.getId());
			return OrderStatusUpdateResponse.builder()
					.orderId(orderId)
					.orderStatus(order.getOrderStatus())
					.build();
		} catch (ServiceException e) {
			log.error("Error while updating order status: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while updating order status: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}
	public RestaurantOwnerResponse registerRestaurantOwner(RestaurantOwnerRequest restaurantOwnerRequest) {
		try {
			// Check if a restaurant owner with the given contact number already exists
			RestaurantOwner existingRestaurantOwner = restaurantOwnerRepository
					.findByMobileNumber(restaurantOwnerRequest.getMobileNumber());

			if(existingRestaurantOwner != null) {
				log.error("Restaurant owner with contact number {} already exists", restaurantOwnerRequest.getMobileNumber());
				throw new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant owner with contact number " + restaurantOwnerRequest.getMobileNumber() + " already exists");
			}
            // Create a new restaurant owner
			RestaurantOwner newRestaurantOwner = RestaurantOwner.builder().name(restaurantOwnerRequest.getName())
					.mobileNumber(restaurantOwnerRequest.getMobileNumber()).email(restaurantOwnerRequest.getEmail())
					.isDeleted(false).isArchived(false)
					.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

			// Save the new restaurant owner to the database
			restaurantOwnerRepository.save(newRestaurantOwner);

			//create login details
			loginService.addLoginDetails(restaurantOwnerRequest.getMobileNumber(), restaurantOwnerRequest.getPassword(), UserTypes.RESTAURANT_OWNER.name());

			log.info("Restaurant owner added successfully with contact number: {}", restaurantOwnerRequest.getMobileNumber());

			return RestaurantOwnerResponse.builder().ownerId(newRestaurantOwner.getId()).name(newRestaurantOwner.getName())
					.mobileNumber(newRestaurantOwner.getMobileNumber()).email(newRestaurantOwner.getEmail()).build();

		} catch (ServiceException e) {
			log.error("Error while registering restaurant owner: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while registering restaurant owner: {}", e.getMessage());
			throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
		}
	}

	private Boolean isValidRestaurantOwner(Long restaurantOwnerId, Long restaurantId) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
		if(restaurant == null) {
			log.error("Restaurant with ID {} not found", restaurantId);
			throw new ServiceException(HttpStatus.NOT_FOUND, "Restaurant with ID " + restaurantId + " not found");
		}
		return restaurant.getOwner().getId().equals(restaurantOwnerId);
	}
}
