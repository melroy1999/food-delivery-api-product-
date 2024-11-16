package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.delivery.ProcessDeliveryRequest;
import com.assignment.fooddelivery.dto.delivery.ProcessDeliveryResponse;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.repository.DeliveryAgentRepository;
import com.assignment.fooddelivery.statemachine.OrderEvents;
import com.assignment.fooddelivery.statemachine.OrderStates;
import com.assignment.fooddelivery.statemachine.ProcessOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Slf4j
@Service
public class DeliveryService {
    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private ProcessOrderEvent processOrderEvent;
    @Autowired
    private OrderService orderService;

    public ProcessDeliveryResponse processOrderDelivery(ProcessDeliveryRequest processDeliveryRequest, Long orderId) {
        try {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(processDeliveryRequest.getDeliveryAgentId()).orElse(null);
            if (deliveryAgent == null) {
                log.error("Delivery agent with id {} not found", processDeliveryRequest.getDeliveryAgentId());
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Delivery agent with id " + processDeliveryRequest.getDeliveryAgentId() + " not found");
            }
            if (!isOrderEventValid(processDeliveryRequest.getDeliveryStatus().getOrderEvent())) {
                log.error("Invalid order status: {}", processDeliveryRequest.getDeliveryStatus().getOrderEvent());
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid order status: " + processDeliveryRequest.getDeliveryStatus().getOrderEvent());
            }
            Order order = orderService.getOrderByOrderId(orderId);
            if (order.getOrderStatus() == OrderStates.DELIVERED) {
                log.error("Order with id {} is already delivered", orderId);
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Order with id " + orderId + " is already delivered");
            }
            OrderEvents orderEvent = OrderEvents.valueOf(processDeliveryRequest.getDeliveryStatus().getOrderEvent());
            processOrderEvent.process(order.getId(), orderEvent, processDeliveryRequest.getDeliveryStatus().getComment(), UserTypes.DELIVERY_AGENT, deliveryAgent.getId());
            // Process the delivery
            if (processDeliveryRequest.getDeliveryStatus().getOrderEvent().equals(OrderEvents.ACCEPT_DELIVERY)) {
                orderService.addOrderDeliveryAgentMapping(order, deliveryAgent);
            }
            order = orderService.getOrderByOrderId(orderId);
            log.info("Delivery processed successfully by agent: {}", deliveryAgent.getId());
            return ProcessDeliveryResponse.builder()
                    .orderId(orderId)
                    .orderStatus(order.getOrderStatus())
                    .build();
        } catch (IllegalStateException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Delivery cannot be processed: " + e.getMessage());
        } catch (ServiceException e) {
            log.error("Error while processing delivery: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while processing delivery: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isOrderEventValid(String orderStatus) {
        return EnumSet.allOf(OrderEvents.class)
                .stream()
                .anyMatch(state -> state.name().equals(orderStatus));
    }
}
