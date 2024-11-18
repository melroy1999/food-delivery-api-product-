package com.assignment.fooddelivery.dto.admin;

import com.assignment.fooddelivery.enums.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeactivateUserRequest {
    private String username;
    private UserTypes userType;
}
