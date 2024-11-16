package com.assignment.fooddelivery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.service.RestaurantService;

@RestController
public class CustomerController {
	
	@Autowired
	RestaurantService restaurantService;
	
	@GetMapping("/api/customer/restaurant")
	public ResponseEntity<List<Restaurant>> getResturant(){
		List<Restaurant> restaurants = restaurantService.getAllRestaurants();
		if(restaurants.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
