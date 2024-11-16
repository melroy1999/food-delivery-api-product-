package com.assignment.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class FooddeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FooddeliveryApplication.class, args);
	}

}
