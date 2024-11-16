package com.assignment.fooddelivery.statemachine;

import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
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
    private OrderRepository orderRepository;

    @OnStateChanged
    public void onStateChanged(StateMachine<OrderStates, OrderEvents> stateMachine) {
        Long orderId = (Long) stateMachine.getExtendedState().getVariables().get("orderId");

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is missing in the state machine context");
        }

        // Get the new state directly from the state machine
        OrderStates newState = stateMachine.getState().getId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(HttpStatus.BAD_REQUEST, "Order not found"));

        //validateStateTransition(order.getOrderStatus(), newState);
        order.setOrderStatus(newState);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @OnEventNotAccepted
    public void onEventNotAccepted(StateMachine<OrderStates, OrderEvents> stateMachine) {
        Long orderId = (Long) stateMachine.getExtendedState().getVariables().get("orderId");

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is missing in the state machine context");
        }

        throw new ServiceException(HttpStatus.BAD_REQUEST, "Invalid event for the current state " + stateMachine.getState().getId());
    }

    private void validateStateTransition(OrderStates currentState, OrderStates newState) {
        switch (currentState) {
            case PLACED:
                if (newState != OrderStates.CONFIRMED && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case CONFIRMED:
                if (newState != OrderStates.PREPARING && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case PREPARING:
                if (newState != OrderStates.READY && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case READY:
                if (newState != OrderStates.ACCEPTED_BY_DELIVERY_AGENT && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case ACCEPTED_BY_DELIVERY_AGENT:
                if (newState != OrderStates.OUT_FOR_DELIVERY && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newState != OrderStates.DELIVERED && newState != OrderStates.CANCELLED) {
                    throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
                }
                break;
            case DELIVERED:
                throw new IllegalStateException("No transitions allowed from the DELIVERED state");
            default:
                throw new IllegalStateException("Invalid state transition from " + currentState + " to " + newState);
        }
    }
}