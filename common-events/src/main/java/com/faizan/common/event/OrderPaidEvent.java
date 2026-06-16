package com.faizan.common.event;

import java.time.Instant;

/**
 * Published by Payment Service after a payment is processed successfully.
 */
public record OrderPaidEvent(
        String eventId,
        Long orderId,
        String customerEmail,
        String paymentId,
        Instant occurredAt
) {}
