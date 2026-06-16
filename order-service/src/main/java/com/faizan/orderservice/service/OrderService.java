package com.faizan.orderservice.service;

import com.faizan.common.event.OrderCreatedEvent;
import com.faizan.orderservice.dto.CreateOrderRequest;
import com.faizan.orderservice.entity.Order;
import com.faizan.orderservice.entity.OrderStatus;
import com.faizan.orderservice.event.OrderEventProducer;
import com.faizan.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer producer;

    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        Order order = new Order();
        order.setCustomerEmail(req.customerEmail());
        order.setProductId(req.productId());
        order.setQuantity(req.quantity());
        order.setAmount(req.amount());
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);
        log.info("Order created with id={}, status=PENDING", saved.getId());

        // Publish the event so downstream services pick it up.
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                saved.getId(),
                saved.getCustomerEmail(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getAmount(),
                Instant.now()
        );
        producer.publishOrderCreated(event);

        return saved;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}
