package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.delivery.*;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.service.DeliveryAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/delivery-agent")
public class DeliveryAgentController {
    @Autowired
    private DeliveryAgentService deliveryAgentService;

    @PostMapping("/register")
    public ApiResponse<DeliveryAgentResponse> addDeliveryAgent(@RequestBody DeliveryAgentRequest deliveryAgent) {
        try {
            DeliveryAgentResponse response = deliveryAgentService.addDeliveryAgent(deliveryAgent);
            return ApiResponse.<DeliveryAgentResponse>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while adding delivery agent with mobile number: {}", deliveryAgent.getMobileNumber(), e);
            return ApiResponse.<DeliveryAgentResponse>builder().status("failed")
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
    @PostMapping("/{id}/update")
    public ApiResponse<DeliveryAgentResponse> updateDeliveryAgent(@RequestBody DeliveryAgentUpdateRequest deliveryAgent, @PathVariable("id") Long id) {
        try {
            DeliveryAgentResponse response = deliveryAgentService.updateDeliveryAgent(deliveryAgent);
            return ApiResponse.<DeliveryAgentResponse>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while updating delivery agent with id: {}", deliveryAgent.getId(), e);
            return ApiResponse.<DeliveryAgentResponse>builder().status("failed")
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

    @GetMapping("/{id}/order-for-pickup")
    public ApiResponse<List<ReadyToDeliverOrder>> getOrdersForPickup(@PathVariable("id") Long id) {
        try {
            List<ReadyToDeliverOrder> response = deliveryAgentService.getReadyForDeliveryOrders(id);
            return ApiResponse.<List<ReadyToDeliverOrder>>builder().status("success").data(response).build();
        } catch (ServiceException e) {
            log.error("ServiceException occured while getting orders for pickup for delivery agent with id: {}", id, e);
            return ApiResponse.<List<ReadyToDeliverOrder>>builder().status("failed")
                    .error(ErrorMessage.builder().error("Error while getting orders for pickup").description(e.getMessage()).build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while getting orders for pickup for delivery agent with id: {}", id, e);
            return ApiResponse.<List<ReadyToDeliverOrder>>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while getting orders for pickup").description(e.getMessage()).build()
            ).build();
        }
    }
}
