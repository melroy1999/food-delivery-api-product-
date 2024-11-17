package com.assignment.fooddelivery.service;
import com.assignment.fooddelivery.dto.delivery.*;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.enums.VehicleTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.Restaurant;
import com.assignment.fooddelivery.repository.DeliveryAgentRepository;
import com.assignment.fooddelivery.statemachine.ProcessOrderEvent;
import com.assignment.fooddelivery.utils.CommonOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeliveryAgentService {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProcessOrderEvent processOrderEvent;
    @Autowired
    private CommonOperations commonOperations;

    public DeliveryAgentResponse addDeliveryAgent(DeliveryAgentRequest deliveryAgent) {
        try{
            DeliveryAgent existingAgent = deliveryAgentRepository.findByMobileNumber(deliveryAgent.getMobileNumber());
            if (existingAgent != null) {
                log.error("Delivery agent with mobile number {} already exists", deliveryAgent.getMobileNumber());
                throw new ServiceException(HttpStatus.BAD_REQUEST,"Delivery agent with mobile number " + deliveryAgent.getMobileNumber() + " already exists");
            }
            Restaurant restaurant = restaurantService.getRestaurantById(deliveryAgent.getRestaurantId());
            if(restaurant == null) {
                log.error("Restaurant with id {} not found", deliveryAgent.getRestaurantId());
                throw new ServiceException(HttpStatus.BAD_REQUEST,"Restaurant with id " + deliveryAgent.getRestaurantId() + " not found");
            }
            // Add delivery agent to database
            DeliveryAgent delAgent = DeliveryAgent.builder()
                    .name(deliveryAgent.getName())
                    .mobileNumber(deliveryAgent.getMobileNumber())
                    .vehicleType(VehicleTypes.valueOf(deliveryAgent.getVehicleType().toUpperCase()))
                    .restaurant(restaurant)
                    .isActive(true)
                    .isWorking(true)
                    .isDeleted(false)
                    .isArchived(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            deliveryAgentRepository.save(delAgent);
            log.info("Delivery agent added successfully with mobile number: {}", deliveryAgent.getMobileNumber());
            delAgent = deliveryAgentRepository.findByMobileNumber(deliveryAgent.getMobileNumber());
            if(delAgent == null) {
                log.error("Error while fetching delivery agent with mobile number: {}", deliveryAgent.getMobileNumber());
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching delivery agent with mobile number: " + deliveryAgent.getMobileNumber());
            }
            loginService.addLoginDetails(deliveryAgent.getMobileNumber(), deliveryAgent.getPassword(), UserTypes.DELIVERY_AGENT.name());
            return DeliveryAgentResponse.builder()
                    .agentId(delAgent.getId())
                    .name(delAgent.getName())
                    .mobileNo(delAgent.getMobileNumber())
                    .vehicleType(deliveryAgent.getVehicleType())
                    .restaurantId(deliveryAgent.getRestaurantId())
                    .status("ACTIVE")
                    .isActive(true)
                    .isWorking(true)
                    .build();
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided vehicleType is invalid
            throw new ServiceException(HttpStatus.BAD_REQUEST,"Invalid vehicle type: " + deliveryAgent.getVehicleType());
        }
        catch (ServiceException e) {
            log.error("Error while adding delivery agent: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error while adding delivery agent: {}", e.getMessage());
            throw e;
        }
    }

    public DeliveryAgentResponse updateDeliveryAgent(DeliveryAgentUpdateRequest deliveryAgent) {
        try {
            DeliveryAgent existingAgent = deliveryAgentRepository.findById(deliveryAgent.getId()).orElse(null);
            if (existingAgent == null) {
                log.error("Delivery agent with id {} not found", deliveryAgent.getId());
                throw new ServiceException(HttpStatus.BAD_REQUEST,"Delivery agent with id " + deliveryAgent.getId() + " not found");
            }
            existingAgent.setRestaurant(restaurantService.getRestaurantById(deliveryAgent.getRestaurantId()));
            existingAgent.setVehicleType(VehicleTypes.valueOf(deliveryAgent.getVehicleType().toUpperCase()));
            existingAgent.setIsActive(deliveryAgent.getIsActive());
            existingAgent.setIsWorking(deliveryAgent.getIsWorking());
            existingAgent.setUpdatedAt(LocalDateTime.now());
            deliveryAgentRepository.save(existingAgent);
            log.info("Delivery agent updated successfully with id: {}", deliveryAgent.getId());
            return DeliveryAgentResponse.builder()
                    .agentId(existingAgent.getId())
                    .name(existingAgent.getName())
                    .mobileNo(existingAgent.getMobileNumber())
                    .vehicleType(deliveryAgent.getVehicleType())
                    .restaurantId(existingAgent.getRestaurant().getId())
                    .status(existingAgent.getIsActive() ? "ACTIVE" : "INACTIVE")
                    .isActive(existingAgent.getIsActive())
                    .isWorking(existingAgent.getIsWorking())
                    .build();
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided vehicleType is invalid
            throw new ServiceException(HttpStatus.BAD_REQUEST,"Invalid vehicle type: " + deliveryAgent.getVehicleType());
        }
        catch (ServiceException e) {
            log.error("Error while updating delivery agent: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error while updating delivery agent: {}", e.getMessage());
            throw e;
        }
    }

    public List<ReadyToDeliverOrder> getReadyForDeliveryOrders (Long deliveryAgentId) {
        try {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(deliveryAgentId).orElse(null);
            if (deliveryAgent == null) {
                log.error("Delivery agent with id {} not found", deliveryAgentId);
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Delivery agent with id " + deliveryAgentId + " not found");
            }
            List<Order> deliverables = orderService.getReadyForDeliveryOrders(deliveryAgentId, deliveryAgent.getRestaurant().getId());

            List<ReadyToDeliverOrder> readyToDeliverOrders = deliverables.stream()
                    .map(order -> new ReadyToDeliverOrder(
                            order.getId(),
                            order.getCustomerAddress(),
                            order.getCustomer().getName(),
                            order.getCustomer().getMobileNumber(),
                            commonOperations.getOrderMenuDetails(order.getOrderDetails()),
                            order.getTotalAmount()
                    ))
                    .collect(Collectors.toList());
            return readyToDeliverOrders;
        } catch (ServiceException e) {
            log.error("Error while fetching ready for delivery orders: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching ready for delivery orders: {}", e.getMessage());
            throw e;
        }
    }
}
