package com.assignment.fooddelivery.statemachine;

import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.exception.ServiceException;
import com.assignment.fooddelivery.model.Order;
import com.assignment.fooddelivery.model.OrderState;
import com.assignment.fooddelivery.repository.OrderRepository;
import com.assignment.fooddelivery.repository.OrderStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StateMachinePersister<OrderStates, OrderEvents, String> persister;
    @Autowired
    private JpaRepositoryStateMachinePersist jpaRepositoryStateMachinePersist;

    public OrderStates process(Long orderId, OrderEvents event) {
        try {
            // Retrieve the state machine context from the database
            StateMachineContext<OrderStates, OrderEvents> context = jpaRepositoryStateMachinePersist.read(orderId.toString());

            // Create or retrieve the state machine
            StateMachine<OrderStates, OrderEvents> stateMachine = stateMachineFactory.getStateMachine(String.valueOf(orderId));

            // Start the state machine if it's not already started
            stateMachine.start();

            // Set the restored context
            if (context != null) {
                // Use the state and event from the context to initialize the state machine
                stateMachine.getStateMachineAccessor().doWithAllRegions(sm -> {
                    sm.resetStateMachine(new DefaultStateMachineContext<>(
                            context.getState(), context.getEvent() , null, null));
                });
            }

            // Now you can send the event and process the order
            boolean eventSent = stateMachine.sendEvent(event);
            if(eventSent) {
                persister.persist(stateMachine, orderId.toString());
            }

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
