package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.delivery.ProcessDeliveryRequest;
import com.assignment.fooddelivery.dto.delivery.ProcessDeliveryResponse;
import com.assignment.fooddelivery.dto.delivery.ReadyToDeliverOrder;
import com.assignment.fooddelivery.dto.order.OrderMenuDetails;
import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.repository.DeliveryAgentRepository;
import com.assignment.fooddelivery.statemachine.OrderEvents;
import com.assignment.fooddelivery.statemachine.OrderStates;
import com.assignment.fooddelivery.statemachine.ProcessOrderEvent;
import com.assignment.fooddelivery.utils.CommonOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliveryService {
    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private ProcessOrderEvent processOrderEvent;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonOperations commonOperations;

    public ProcessDeliveryResponse processOrderDelivery(ProcessDeliveryRequest processDeliveryRequest, Long orderId) {
        try {
            if(!commonOperations.isEligibleOrderEvent(UserTypes.DELIVERY_AGENT.name(), OrderEvents.valueOf(processDeliveryRequest.getOrderStatus().getOrderEvent()))) {
                log.error("Delivery agent is not eligible to perform this action");
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Delivery agent is not eligible to perform this action");
            }
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(processDeliveryRequest.getDeliveryAgentId()).orElse(null);
            if (deliveryAgent == null) {
                log.error("Delivery agent with id {} not found", processDeliveryRequest.getDeliveryAgentId());
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Delivery agent with id " + processDeliveryRequest.getDeliveryAgentId() + " not found");
            }
            if (!commonOperations.isOrderEventValid(processDeliveryRequest.getOrderStatus().getOrderEvent())) {
                log.error("Invalid order status: {}", processDeliveryRequest.getOrderStatus().getOrderEvent());
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid order status: " + processDeliveryRequest.getOrderStatus().getOrderEvent());
            }
            Order order = orderService.getOrderByOrderId(orderId);
            if (order.getOrderStatus() == OrderStates.DELIVERED) {
                log.error("Order with id {} is already delivered", orderId);
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Order with id " + orderId + " is already delivered");
            }
            OrderEvents orderEvent = OrderEvents.valueOf(processDeliveryRequest.getOrderStatus().getOrderEvent());
            OrderStates orderStates = processOrderEvent.process(order.getId(), orderEvent, processDeliveryRequest.getOrderStatus().getComment(), UserTypes.DELIVERY_AGENT, deliveryAgent.getId());
            if(order.getOrderStatus().ordinal() >= orderStates.ordinal()) {
                log.error("Order state transition failed for order with id: {}", orderId);
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Order state transition failed for order with id: " + orderId);
            }
            // Process the delivery
            if (processDeliveryRequest.getOrderStatus().getOrderEvent().equals(OrderEvents.ACCEPT_DELIVERY.name())) {
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
}
