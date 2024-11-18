package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.admin.AdminSignup;
import com.assignment.fooddelivery.dto.admin.AdminSignupResponse;
import com.assignment.fooddelivery.dto.admin.DeactivateUserRequest;
import com.assignment.fooddelivery.dto.admin.DeactivateUserResponse;
import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentResponse;
import com.assignment.fooddelivery.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/signup")
    public ApiResponse<AdminSignupResponse> signup(@RequestBody  AdminSignup adminSignup) {
        try {
            AdminSignupResponse response = adminService.signup(adminSignup);
            return ApiResponse.<AdminSignupResponse>builder().status("success").data(response).build();
        } catch (Exception e) {
            log.error("Exception occured while adding admin with username: {}", adminSignup.getUsername(), e);
            return ApiResponse.<AdminSignupResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while signup admin").description(e.getMessage()).build()
            ).build();
        }
    }

    @PostMapping("/deactivate")
    public ApiResponse<DeactivateUserResponse> deactivateuser(@RequestBody  DeactivateUserRequest deactivateUserRequest) {
        try {
            DeactivateUserResponse response = adminService.deactivateUser(deactivateUserRequest);
            return ApiResponse.<DeactivateUserResponse>builder().status("success").data(response).build();
        } catch (Exception e) {
            log.error("Exception occured while deactivating user with username: {}", deactivateUserRequest.getUsername(), e);
            return ApiResponse.<DeactivateUserResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while deactivating user").description(e.getMessage()).build()
            ).build();
        }
    }

    @PostMapping("/activate")
    public ApiResponse<DeactivateUserResponse> activateuser(@RequestBody DeactivateUserRequest deactivateUserRequest) {
        try {
            DeactivateUserResponse response = adminService.activateUser(deactivateUserRequest);
            return ApiResponse.<DeactivateUserResponse>builder().status("success").data(response).build();
        } catch (Exception e) {
            log.error("Exception occured while activating user with username: {}", deactivateUserRequest.getUsername(), e);
            return ApiResponse.<DeactivateUserResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while activating user").description(e.getMessage()).build()
            ).build();
        }
    }
}
