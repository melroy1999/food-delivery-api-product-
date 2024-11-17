package com.assignment.fooddelivery.service;

import com.assignment.fooddelivery.dto.delivery.ReadyToDeliverOrder;
import com.assignment.fooddelivery.dto.order.OrderLogResponse;
import com.assignment.fooddelivery.dto.order.OrderRequest;
import com.assignment.fooddelivery.dto.order.OrderResponse;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.*;
import com.assignment.fooddelivery.repository.*;
import com.assignment.fooddelivery.statemachine.OrderStates;
import com.assignment.fooddelivery.utils.CommonOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDeliveryAgentRepository orderDeliveryAgentRepository;
    @Autowired
    private OrderLogRepository orderLogRepository;
    @Autowired
    private CommonOperations commonOperations;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private CustomerRepository customerRepository;

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

    public List<Order> getReadyForDeliveryOrders(Long deliveryAgentId, Long restaurantId) {
        List<Order> eligibleOrders = orderRepository.getReadyForDeliveryOrders(deliveryAgentId, restaurantId);
        if(eligibleOrders.isEmpty()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "No orders found ready for delivery agent: " + deliveryAgentId);
        }
        return eligibleOrders;
    }

    public OrderResponse createOrder(OrderRequest orderRequest){
        try{
            Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurantId()).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant not found with id: " + orderRequest.getRestaurantId()));
            if(restaurant == null) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Restaurant is not available for orders");
            }
            Customer customer = customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Customer not found with id: " + orderRequest.getCustomerId()));
            if(customer == null) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, "Customer is not available for orders");
            }
            Order order = Order.builder()
                    .orderDetails(commonOperations.getOrderMenuDetailsJson(orderRequest.getItems()))
                    .restaurant(restaurant)
                    .customer(customer)
                    .orderStatus(OrderStates.PLACED)
                    .totalAmount(orderRequest.getTotalAmount())
                    .customerAddress(orderRequest.getDeliveryAddress())
                    .isDeleted(false)
                    .isArchived(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order);
            return OrderResponse.builder().orderId(order.getId()).orderStatus(order.getOrderStatus().name()).build();
        }catch (ServiceException e){
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating order");
        }
    }

    public Order trackOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Order not found with id: " + orderId));
    }

    public List<OrderLogResponse> getOrderLogs(Long orderId) {
        try {
            List<OrderLog> orderLogs = orderLogRepository.findByOrderId(orderId);
            if (orderLogs.isEmpty()) {
                throw new ServiceException(HttpStatus.BAD_REQUEST, "No logs found for order: " + orderId);
            }
            List<OrderLogResponse> orderLogResponses = new ArrayList<>();

            for (OrderLog orderLog : orderLogs) {
                orderLogResponses.add(OrderLogResponse.builder()
                        .orderStatus(orderLog.getOrderSubStatus())
                        .remarks(orderLog.getRemarks())
                        .userType(orderLog.getEnteredBy())
                        .userId(orderLog.getEnteredById())
                        .timestamp(orderLog.getCreatedAt().toString())
                        .build());
            }
            return orderLogResponses;
        }catch (ServiceException e){
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching order logs");
        }
    }
}
