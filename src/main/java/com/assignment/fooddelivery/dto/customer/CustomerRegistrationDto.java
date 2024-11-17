package com.assignment.fooddelivery.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String address;
    private String mobileNumber;
}
