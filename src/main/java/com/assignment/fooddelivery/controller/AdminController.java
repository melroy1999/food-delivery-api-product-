package com.assignment.fooddelivery.controller;

import com.assignment.fooddelivery.dto.admin.AdminSignup;
import com.assignment.fooddelivery.dto.admin.AdminSignupResponse;
import com.assignment.fooddelivery.dto.admin.DeactivateUserRequest;
import com.assignment.fooddelivery.dto.admin.DeactivateUserResponse;
import com.assignment.fooddelivery.dto.common.ApiResponse;
import com.assignment.fooddelivery.dto.common.ErrorMessage;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentResponse;
import com.assignment.fooddelivery.dto.delivery.DeliveryAgentUpdateRequest;
import com.assignment.fooddelivery.dto.delivery.ReadyToDeliverOrder;
import com.assignment.fooddelivery.dto.order.OrderLogResponse;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.service.AdminService;
import com.assignment.fooddelivery.service.DeliveryAgentService;
import com.assignment.fooddelivery.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryAgentService deliveryAgentService;

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

    @GetMapping("/orders/{orderId}/status")
    public ApiResponse<Order> trackOrder(@PathVariable Long orderId) {
        try {
            return ApiResponse.<Order>builder().status("success").data(orderService.getOrderByOrderId(orderId)).build();
        }catch (ServiceException e) {
            log.error("ServiceException occured while getting order with id: {}", orderId, e);
            return ApiResponse.<Order>builder().status("failed")
                    .error(ErrorMessage.builder().error("Error while getting order").description(e.getMessage()).build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while getting order with id: {}", orderId, e);
            return ApiResponse.<Order>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while getting order").description(e.getMessage()).build()
            ).build();
        }
    }

    // View Order History
    @GetMapping("/order/{orderId}/history")
    public ApiResponse<List<OrderLogResponse>> getOrderHistory(@PathVariable("orderId") Long orderId) {
        try{
            return ApiResponse.<List<OrderLogResponse>>builder().status("success").data(orderService.getOrderLogs(orderId)).build();
        }catch (ServiceException e) {
            log.error("ServiceException occured while getting order logs with id: {}", orderId, e);
            return ApiResponse.<List<OrderLogResponse>>builder().status("failed")
                    .error(ErrorMessage.builder().error("Error while getting order logs").description(e.getMessage()).build())
                    .build();
        }
        catch (Exception e) {
            log.error("Exception occured while getting order logs with id: {}", orderId, e);
            return ApiResponse.<List<OrderLogResponse>>builder().status("failed").error(
                    ErrorMessage.builder().error("Error while getting order logs").description(e.getMessage()).build()
            ).build();
        }
    }

    @PutMapping("/{id}/delivery-agent")
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
