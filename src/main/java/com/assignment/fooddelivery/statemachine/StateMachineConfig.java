package com.assignment.fooddelivery.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStates.PLACED)
                .state(OrderStates.CONFIRMED)
                .state(OrderStates.PREPARING)
                .state(OrderStates.READY)
                .state(OrderStates.ACCEPTED_BY_DELIVERY_AGENT)
                .state(OrderStates.OUT_FOR_DELIVERY)
                .end(OrderStates.DELIVERED)
                .end(OrderStates.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStates.PLACED).target(OrderStates.CONFIRMED).event(OrderEvents.CONFIRM)
                .and()
                .withExternal().source(OrderStates.CONFIRMED).target(OrderStates.PREPARING).event(OrderEvents.START_PREPARING)
                .and()
                .withExternal().source(OrderStates.PREPARING).target(OrderStates.READY).event(OrderEvents.MARK_READY)
                .and()
                .withExternal().source(OrderStates.READY).target(OrderStates.ACCEPTED_BY_DELIVERY_AGENT).event(OrderEvents.ACCEPT_DELIVERY)
                .and()
                .withExternal().source(OrderStates.ACCEPTED_BY_DELIVERY_AGENT).target(OrderStates.OUT_FOR_DELIVERY).event(OrderEvents.START_DELIVERY)
                .and()
                .withExternal().source(OrderStates.OUT_FOR_DELIVERY).target(OrderStates.DELIVERED).event(OrderEvents.COMPLETE_DELIVERY)
                .and()
                .withExternal().source(OrderStates.PLACED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.CONFIRMED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.PREPARING).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.READY).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.ACCEPTED_BY_DELIVERY_AGENT).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.OUT_FOR_DELIVERY).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL);
    }
}