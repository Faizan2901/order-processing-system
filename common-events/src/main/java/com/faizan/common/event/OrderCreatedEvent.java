package com.faizan.common.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published by Order Service when a new order is created (status PENDING).
 * eventId is used for idempotency on the consumer side.
 */
public record OrderCreatedEvent(
        String eventId,
        Long orderId,
        String customerEmail,
        String productId,
        int quantity,
        BigDecimal amount,
        Instant occurredAt
) {}
