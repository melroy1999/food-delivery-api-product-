package com.assignment.fooddelivery.controller.test;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.assignment.fooddelivery.controller.RestaurantController;
import com.assignment.fooddelivery.dto.restaurant.MenuItemRequest;
import com.assignment.fooddelivery.dto.restaurant.MenuItemResponse;
import com.assignment.fooddelivery.dto.restaurant.OrderResponse;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerRequest;
import com.assignment.fooddelivery.dto.restaurant.RestaurantOwnerResponse;
import com.assignment.fooddelivery.service.RestaurantService;
import com.assignment.fooddelivery.statemachine.OrderStates;

class RestaurantControllerTest {

	private MockMvc mockMvc;

	@Mock
	private RestaurantService restaurantService;

	@InjectMocks
	private RestaurantController restaurantController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
	}

	@Test
	void testRegisterOwner_Success() throws Exception {
		RestaurantOwnerRequest ownerRequest = new RestaurantOwnerRequest();
		RestaurantOwnerResponse ownerResponse = new RestaurantOwnerResponse();

		when(restaurantService.registerOwner(any(RestaurantOwnerRequest.class))).thenReturn(ownerResponse);

		mockMvc.perform(post("/restaurant-owner/register").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"Test Owner\", \"email\": \"owner@test.com\"}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("success")));
	}

	@Test
	void testAddMenuToRestaurant_Success() throws Exception {
		MenuItemRequest menuItemRequest = new MenuItemRequest();
		MenuItemResponse menuItemResponse = new MenuItemResponse();

		List<MenuItemResponse> menuResponses = Collections.singletonList(menuItemResponse);

		when(restaurantService.addMenuToRestaurant(anyLong(), any(List.class))).thenReturn(menuResponses);

		mockMvc.perform(post("/restaurant/1/menu").contentType(MediaType.APPLICATION_JSON)
				.content("[{\"itemName\": \"Pizza\", \"itemPrice\": 9.99}]")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("success")));
	}

	@Test
	void testUpdateRestaurant_Success() throws Exception {
		RestaurantOwnerRequest updatedRequest = new RestaurantOwnerRequest();
		RestaurantOwnerResponse updatedResponse = new RestaurantOwnerResponse();

		when(restaurantService.updateRestaurant(anyLong(), any(RestaurantOwnerRequest.class)))
				.thenReturn(updatedResponse);

		mockMvc.perform(put("/update-restaurant/1").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"Updated Restaurant\", \"address\": \"123 Main St\"}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("success")));
	}

	@Test
	void testUpdateMenuItem_Success() throws Exception {
		MenuItemRequest menuItemRequest = new MenuItemRequest();
		List<MenuItemResponse> menuItemResponses = Collections.singletonList(new MenuItemResponse());

		when(restaurantService.updateMenuItem(anyLong(), any(List.class))).thenReturn(menuItemResponses);

		mockMvc.perform(put("/menu/1").contentType(MediaType.APPLICATION_JSON)
				.content("[{\"itemName\": \"Updated Pizza\", \"itemPrice\": 10.99}]")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("success")));
	}

	@Test
	void testGetOrdersByRestaurant_Success() throws Exception {
		OrderResponse orderResponse = new OrderResponse();
		List<OrderResponse> orderResponses = Collections.singletonList(orderResponse);

		when(restaurantService.getOrderByRestaurantId(anyLong())).thenReturn(orderResponses);

		mockMvc.perform(get("/restaurant/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is("success")));
	}

	@Test
	void testUpdateOrderStatus_Success() throws Exception {

		Long orderId = 1L;
		String newStatus = "DELIVERED";
		OrderResponse expectedResponse = new OrderResponse();

		when(restaurantService.updateOrderStatus(eq(orderId), eq(OrderStates.DELIVERED))).thenReturn(expectedResponse);

		mockMvc.perform(post("/order/{orderId}/status", orderId).contentType(MediaType.APPLICATION_JSON)
				.content("{\"newStatus\": \"" + newStatus + "\"}")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success")).andExpect(jsonPath("$.data").isNotEmpty());

		verify(restaurantService, times(1)).updateOrderStatus(orderId, OrderStates.DELIVERED);
	}

}
