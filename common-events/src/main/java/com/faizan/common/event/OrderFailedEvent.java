package com.faizan.common.event;

import java.time.Instant;

/**
 * Published when an order fails at any stage (out of stock, payment declined).
 * Consumed by Order (mark FAILED), Inventory (release stock - compensation),
 * and Notification (inform customer).
 */
public record OrderFailedEvent(
        String eventId,
        Long orderId,
        String customerEmail,
        String productId,
        int quantity,
        String reason,
        Instant occurredAt
) {}
