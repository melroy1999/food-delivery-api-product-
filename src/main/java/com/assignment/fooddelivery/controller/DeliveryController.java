package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentRequest;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentResponse;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;

    public ApiResponse<DeliveryAgentResponse> addDeliveryAgent(DeliveryAgentRequest deliveryAgent) {
        try {
            DeliveryAgentResponse response = deliveryService.addDeliveryAgent(deliveryAgent);
            return ApiResponse.<DeliveryAgentResponse>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while adding delivery agent with mobile number: {}", deliveryAgent.getMobileNumber(), e);
            return ApiResponse.<DeliveryAgentResponse>builder()
                    .error(ErrorMessage.builder().error("Error while create delivery agent master").description(e.getMessage()).build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while adding delivery agent with mobile number: {}", deliveryAgent.getMobileNumber(), e);
            return ApiResponse.<DeliveryAgentResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while create delivery agent master").description(e.getMessage()).build()
            ).build();
        }
    }
}
