package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.admin.AdminSignup;
import com.assignment.fooddelivery.dto.admin.AdminSignupResponse;
import com.assignment.fooddelivery.dto.admin.DeactivateUserRequest;
import com.assignment.fooddelivery.dto.admin.DeactivateUserResponse;
import com.assignment.fooddelivery.enums.UserTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private LoginService loginService;

    public AdminSignupResponse signup(AdminSignup adminSignup) {
        try{
            loginService.addLoginDetails(adminSignup.getUsername(), adminSignup.getPassword(), UserTypes.ADMIN.name());
            return AdminSignupResponse.builder().status("success").build();
        }catch (Exception e){
            log.error("Exception occured while adding admin with username: {}", adminSignup.getUsername(), e);
            return AdminSignupResponse.builder().status("failed").build();
        }
    }

    public DeactivateUserResponse deactivateUser(DeactivateUserRequest deactivateUserRequest) {
        try{
            loginService.deactivateUser(deactivateUserRequest.getUsername(), deactivateUserRequest.getUserType());
            return DeactivateUserResponse.builder().status("success").build();
        }catch (Exception e){
            log.error("Exception occured while deactivating user with username: {}", deactivateUserRequest.getUsername(), e);
            return DeactivateUserResponse.builder().status("failed").build();
        }
    }

    public DeactivateUserResponse activateUser(DeactivateUserRequest deactivateUserRequest) {
        try{
            loginService.activateUser(deactivateUserRequest.getUsername(), deactivateUserRequest.getUserType());
            return DeactivateUserResponse.builder().status("success").build();
        }catch (Exception e){
            log.error("Exception occured while activating user with username: {}", deactivateUserRequest.getUsername(), e);
            return DeactivateUserResponse.builder().status("failed").build();
        }
    }
}
