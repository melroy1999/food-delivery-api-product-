package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderDeliveryAgent;
import com.assignment.fooddelivery.model.OrderLog;
import com.assignment.fooddelivery.repository.OrderDeliveryAgentRepository;
import com.assignment.fooddelivery.repository.OrderLogRepository;
import com.assignment.fooddelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDeliveryAgentRepository orderDeliveryAgentRepository;
    @Autowired
    private OrderLogRepository orderLogRepository;

    public Order getOrderByOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Order not found with id: " + orderId));
    }

    public void addOrderDeliveryAgentMapping(Order order, DeliveryAgent deliveryAgent) {
        try {
            OrderDeliveryAgent existingMapping = orderDeliveryAgentRepository.findByDeliveryAgent(deliveryAgent);
            if (existingMapping != null) {
                existingMapping.setDeliveryAgentId(deliveryAgent.getId());
                orderDeliveryAgentRepository.save(existingMapping);
            } else {
                orderDeliveryAgentRepository.save(OrderDeliveryAgent.builder()
                        .order(order)
                        .deliveryAgent(deliveryAgent)
                        .isDeleted(false)
                        .isArchived(false)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while adding order delivery agent mapping");
        }
    }

    public void updateOrder(Order order) {
        try {
            orderRepository.save(order);
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating order");
        }
    }

    public void updateOrderLog(OrderLog orderLog) {
        try {
            orderLogRepository.save(orderLog);
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updating order log");
        }
    }
}
