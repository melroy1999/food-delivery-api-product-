package com.assignment.fooddelivery.utils;

import com.assignment.fooddelivery.statemachine.OrderEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
@Slf4j
public class CommonOperations {

    public boolean isEligibleOrderEvent(String role, OrderEvents orderEvent) {
        Set<OrderEvents> eligibleEvents;

        switch (role) {
            case "ADMIN":
                eligibleEvents = EnumSet.of(OrderEvents.CONFIRM, OrderEvents.START_PREPARING, OrderEvents.MARK_READY, OrderEvents.ACCEPT_DELIVERY, OrderEvents.START_DELIVERY, OrderEvents.COMPLETE_DELIVERY, OrderEvents.CANCEL);
                break;
            case "DELIVERY_AGENT":
                eligibleEvents = EnumSet.of(OrderEvents.ACCEPT_DELIVERY, OrderEvents.START_DELIVERY, OrderEvents.COMPLETE_DELIVERY);
                break;
            case "RESTAURANT":
                eligibleEvents = EnumSet.of(OrderEvents.CONFIRM, OrderEvents.START_PREPARING, OrderEvents.MARK_READY);
            case "CUSTOMER":
                eligibleEvents = EnumSet.of(OrderEvents.CANCEL);
                break;
            default:
                eligibleEvents = EnumSet.noneOf(OrderEvents.class);
        }

        return eligibleEvents.contains(orderEvent);
    }
}
