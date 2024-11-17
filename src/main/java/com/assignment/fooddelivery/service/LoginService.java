package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.common.LoginResponse;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.LoginDetails;
import com.assignment.fooddelivery.repository.LoginDetailsRepository;
import com.assignment.fooddelivery.utils.Bcrypt;
import com.assignment.fooddelivery.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private Bcrypt bcrypt;
    @Autowired
    private LoginDetailsRepository loginDetailsRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    public void addLoginDetails(String username, String password, String userRole) {
        try {
            // Add login details to database
            LoginDetails loginDetails = LoginDetails.builder()
                    .username(username)
                    .password(bcrypt.getHashedPassword(password))
                    .userRole(UserTypes.valueOf(userRole))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            // Save login details
            loginDetailsRepository.save(loginDetails);
            log.info("Login details added successfully for user: {} with user-role: {}", username, userRole);
        }
        catch (ServiceException e) {
            log.error("Error while adding login details for user: {}", username);
            throw e;
        }
        catch (Exception e) {
            log.error("Error while adding login details for user: {}", username);
            throw e;
        }
    }

    public LoginResponse login(String username, String password) {
        try {
            // Get login details from database
            LoginDetails loginDetails = loginDetailsRepository.findByUsername(username);
            if (loginDetails == null) {
                log.error("User not found with username: {}", username);
                throw new ServiceException(HttpStatus.NOT_FOUND, "User not found with username: " + username);
            }
            // Check password
            if (!bcrypt.checkPassword(password, loginDetails.getPassword())) {
                log.error("Invalid password for user: {}", username);
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid password for user: " + username);
            }
            String token = jwtTokenUtil.generateToken(loginDetails.getUsername(), loginDetails.getUserRole());
            if (token == null) {
                log.error("Error while generating token for user: {}", username);
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while generating token for user: " + username);
            }
            log.info("User logged in successfully: {}", username);
            return LoginResponse.builder()
                    .token(token)
                    .build();
        }
        catch (ServiceException e) {
            log.error("Error while logging in user: {}", username);
            throw e;
        }
        catch (Exception e) {
            log.error("Error while logging in user: {}", username);
            throw e;
        }
    }
}
