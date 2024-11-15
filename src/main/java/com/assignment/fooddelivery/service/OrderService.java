package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.DeliveryAgent;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderDeliveryAgent;
import com.assignment.fooddelivery.repository.OrderDeliveryAgentRepository;
import com.assignment.fooddelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDeliveryAgentRepository orderDeliveryAgentRepository;

    public Order getOrderByOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Order not found with id: " + orderId));
    }

    public void addOrderDeliveryAgentMapping(Order order, DeliveryAgent deliveryAgent) {
        try{
            OrderDeliveryAgent existingMapping = orderDeliveryAgentRepository.findByDeliveryAgent(deliveryAgent);
            if(existingMapping != null) {
                existingMapping.setDeliveryAgentId(deliveryAgent.getId());
                orderDeliveryAgentRepository.save(existingMapping);
            }else {
                orderDeliveryAgentRepository.save(OrderDeliveryAgent.builder()
                        .order(order)
                        .deliveryAgent(deliveryAgent)
                        .build());
            }
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while adding order delivery agent mapping");
        }
    }
}
