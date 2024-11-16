package com.assignment.fooddelivery.statemachine;

import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderLog;
import com.assignment.fooddelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnEventNotAccepted;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@WithStateMachine
public class OrderStateChangeListener {

    @Autowired
    private OrderService orderService;

    @OnStateChanged
    public void onStateChanged(StateMachine<OrderStates, OrderEvents> stateMachine) {
        Long orderId = (Long) stateMachine.getExtendedState().getVariables().get("orderId");

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is missing in the state machine context");
        }

        // Get the new state directly from the state machine
        OrderStates newState = stateMachine.getState().getId();

        Order order = orderService.getOrderByOrderId(orderId);

        if(order.getOrderStatus() != newState && order.getOrderStatus().ordinal() < newState.ordinal()) {
            order.setOrderStatus(newState);
            order.setUpdatedAt(LocalDateTime.now());
            orderService.updateOrder(order);

            OrderLog orderLog = OrderLog.builder()
                    .order(order)
                    .orderSubStatus(newState.name())
                    .remarks((String) stateMachine.getExtendedState().getVariables().get("remarks"))
                    .enteredBy((String) stateMachine.getExtendedState().getVariables().get("enteredByType"))
                    .enteredById((Long) stateMachine.getExtendedState().getVariables().get("enteredById"))
                    .isDeleted(false)
                    .isArchived(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderService.updateOrderLog(orderLog);
        }
    }

    @OnEventNotAccepted
    public void onEventNotAccepted(StateMachine<OrderStates, OrderEvents> stateMachine) {
        Long orderId = (Long) stateMachine.getExtendedState().getVariables().get("orderId");

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is missing in the state machine context");
        }

        throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid event for the current state " + stateMachine.getState().getId());
    }
}