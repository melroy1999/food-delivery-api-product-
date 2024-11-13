package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.LoginDetails;
import com.assignment.fooddelivery.repository.LoginDetailsRepository;
import com.assignment.fooddelivery.utils.Bcrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private Bcrypt bcrypt;
    @Autowired
    private LoginDetailsRepository loginDetailsRepository;
    public void addLoginDetails(String username, String password, String userRole) {
        try {
            // Add login details to database
            LoginDetails loginDetails = LoginDetails.builder()
                    .username(username)
                    .password(bcrypt.getHashedPassword(password))
                    .userRole(UserTypes.valueOf(userRole))
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
}
