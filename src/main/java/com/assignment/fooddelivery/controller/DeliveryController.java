package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.delivery.*;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/api/delivery-agent/register")
    public ApiResponse<DeliveryAgentResponse> addDeliveryAgent(@RequestBody DeliveryAgentRequest deliveryAgent) {
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
    @PostMapping("/api/delivery-agent/{id}/update")
    public ApiResponse<DeliveryAgentResponse> updateDeliveryAgent(@RequestBody DeliveryAgentUpdateRequest deliveryAgent, @PathVariable("id") Long id) {
        try {
            DeliveryAgentResponse response = deliveryService.updateDeliveryAgent(deliveryAgent);
            return ApiResponse.<DeliveryAgentResponse>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while updating delivery agent with id: {}", deliveryAgent.getId(), e);
            return ApiResponse.<DeliveryAgentResponse>builder()
                    .error(ErrorMessage.builder().error("Error while updating delivery agent master").description(e.getMessage()).build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while updating delivery agent with id: {}", deliveryAgent.getId(), e);
            return ApiResponse.<DeliveryAgentResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while updating delivery agent master").description(e.getMessage()).build()
            ).build();
        }
    }
    @PostMapping("/api/order/{orderId}/process-delivery")
    public ApiResponse<ProcessDeliveryResponse> processDelivery(@RequestBody  ProcessDeliveryRequest processDeliveryRequest, @PathVariable("orderId") Long orderId) {
        try {
            ProcessDeliveryResponse response = deliveryService.processOrderDelivery(processDeliveryRequest, orderId);
            return ApiResponse.<ProcessDeliveryResponse>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while processing delivery for order: {}", orderId, e);
            return ApiResponse.<ProcessDeliveryResponse>builder().status("failed")
                    .error(ErrorMessage.builder().error(e.getMessage()).description("Error while processing delivery").build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while processing delivery for order: {}", orderId, e);
            return ApiResponse.<ProcessDeliveryResponse>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while processing delivery").description(e.getMessage()).build()
            ).build();
        }
    }
}
