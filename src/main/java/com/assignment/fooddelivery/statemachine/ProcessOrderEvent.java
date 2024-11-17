package com.assignment.fooddelivery.statemachine;

import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.OrderState;
import com.assignment.fooddelivery.repository.OrderStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ProcessOrderEvent {
    @Autowired
    private StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;
    @Autowired
    private OrderStateRepository orderStateRepository;

    public OrderStates process(Long orderId, OrderEvents event, String remarks, UserTypes userTypes, Long enteredById) {
        try {
            StateMachine<OrderStates, OrderEvents> stateMachine = stateMachineFactory.getStateMachine(String.valueOf(orderId));
            stateMachine.getExtendedState().getVariables().put("orderId", orderId);
            stateMachine.getExtendedState().getVariables().put("remarks", remarks);
            stateMachine.getExtendedState().getVariables().put("enteredByType", userTypes.name());
            stateMachine.getExtendedState().getVariables().put("enteredById", enteredById);
            stateMachine.start();
            stateMachine.sendEvent(event);
            return stateMachine.getState().getId();

        } catch (Exception e) {
            log.error("Error processing orderId: {}", orderId, e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while processing the order event");
        }
    }
    public void persistState(Long orderId, OrderStates state) {
        try {
            Optional<OrderState> existingEntity = orderStateRepository.findByOrderId(orderId);

            // Check if the entity is present, and update it
            existingEntity.ifPresent(entity -> {
                entity.setState(state);  // Set the new state
                entity.setUpdatedAt(LocalDateTime.now());  // Update the updated time
                orderStateRepository.save(entity);  // Save the updated entity
            });

            // If the entity is not present, create a new one
            if (!existingEntity.isPresent()) {
                OrderState newEntity = new OrderState();
                newEntity.setOrderId(orderId);
                newEntity.setState(state);
                newEntity.setCreatedAt(LocalDateTime.now());
                newEntity.setUpdatedAt(LocalDateTime.now());
                orderStateRepository.save(newEntity);  // Save the new entity
            }
        }
        catch (Exception e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while persisting the state");
        }
    }
}
