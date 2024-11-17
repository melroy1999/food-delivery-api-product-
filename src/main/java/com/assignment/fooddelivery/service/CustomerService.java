package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.customer.CustomerRegistrationDto;
import com.assignment.fooddelivery.dto.customer.CustomerResponse;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Customer;
import com.assignment.fooddelivery.model.CustomerAddress;
import com.assignment.fooddelivery.repository.CustomerAddressRepository;
import com.assignment.fooddelivery.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private CustomerAddressRepository customerAddressRepository;


    public CustomerResponse registerCustomer(CustomerRegistrationDto registrationDto) {
        try {
            log.info("Registering customer: {}", registrationDto);
            Customer existingCustomer = customerRepository.findByMobileNumber(registrationDto.getMobileNumber());
            if (existingCustomer != null) {
                log.error("Customer with mobile number {} already exists", registrationDto.getMobileNumber());
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Customer with mobile number " + registrationDto.getMobileNumber() + " already exists");
            }
            Customer customer = Customer.builder()
                    .name(registrationDto.getName())
                    .email(registrationDto.getEmail())
                    .mobileNumber(registrationDto.getMobileNumber())
                    .isDeleted(false)
                    .isArchived(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            customerRepository.save(customer);
            customer = customerRepository.findByMobileNumber(registrationDto.getMobileNumber());
            CustomerAddress customerAddress = CustomerAddress.builder()
                    .customer(customer)
                    .address(registrationDto.getAddress())
                    .isDeleted(false)
                    .isArchived(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            customerAddressRepository.save(customerAddress);
            log.info("Customer registered successfully with mobile number: {}", registrationDto.getMobileNumber());
            loginService.addLoginDetails(registrationDto.getMobileNumber(), registrationDto.getPassword(), UserTypes.CUSTOMER.name());
            return CustomerResponse.builder()
                    .customerId(customer.getId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .address(customerAddress.getAddress())
                    .mobileNumber(registrationDto.getMobileNumber())
                    .build();
        }
        catch (ServiceException e) {
            log.error("Error while adding customer: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error while adding customer: {}", e.getMessage());
            throw e;
        }
    }
}
