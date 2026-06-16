package com.faizan.orderservice.dto;

import com.faizan.orderservice.entity.Order;
import com.faizan.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String customerEmail,
        String productId,
        int quantity,
        BigDecimal amount,
        OrderStatus status,
        String failureReason,
        Instant createdAt
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(), o.getCustomerEmail(), o.getProductId(),
                o.getQuantity(), o.getAmount(), o.getStatus(),
                o.getFailureReason(), o.getCreatedAt()
        );
    }
}
