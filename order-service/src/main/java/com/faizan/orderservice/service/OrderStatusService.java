package com.faizan.orderservice.service;

import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.OrderPaidEvent;
import com.faizan.orderservice.entity.Order;
import com.faizan.orderservice.entity.OrderStatus;
import com.faizan.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;

    @Transactional
    public void markConfirmed(OrderPaidEvent event) {
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            // Idempotent by state: only move PENDING -> CONFIRMED.
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                log.info("Order {} marked CONFIRMED", event.orderId());
            } else {
                log.warn("Order {} already in status {}, ignoring order-paid",
                        event.orderId(), order.getStatus());
            }
        }, () -> log.warn("Order {} not found for order-paid", event.orderId()));
    }

    @Transactional
    public void markFailed(OrderFailedEvent event) {
        orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.FAILED);
                order.setFailureReason(event.reason());
                orderRepository.save(order);
                log.info("Order {} marked FAILED, reason={}", event.orderId(), event.reason());
            } else {
                log.warn("Order {} already in status {}, ignoring order-failed",
                        event.orderId(), order.getStatus());
            }
        }, () -> log.warn("Order {} not found for order-failed", event.orderId()));
    }
}
