package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.common.LoginRequest;
import com.assignment.fooddelivery.dto.common.LoginResponse;
import com.assignment.fooddelivery.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/api/login")
    public LoginResponse login(@RequestBody  LoginRequest loginRequest) {
        try {
            return loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
        } catch (Exception e) {
            log.error("Exception occured while logging in with username: {}", loginRequest.getUsername(), e);
            return null;
        }
    }
}
