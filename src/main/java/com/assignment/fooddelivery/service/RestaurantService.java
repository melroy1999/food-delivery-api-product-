package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    public Restaurant getRestaurantById(Long restaurantId) {
        // Fetch restaurant details from database
        return restaurantRepository.findById(restaurantId).orElse(null);
    }
    
    public List<Restaurant> getAllRestaurants(){
    	return restaurantRepository.findAll();
    }
}
