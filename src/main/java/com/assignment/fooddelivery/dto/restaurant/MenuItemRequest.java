package com.assignment.fooddelivery.dto.restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRequest {
    private Long id; 
    private String itemName;
    private String itemDescription;
    private BigDecimal itemPrice;
    private boolean isAvailable;
}