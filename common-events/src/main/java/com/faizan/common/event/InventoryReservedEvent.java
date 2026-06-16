package com.faizan.common.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published by Inventory Service after stock is successfully reserved.
 */
public record InventoryReservedEvent(
        String eventId,
        Long orderId,
        String customerEmail,
        String productId,
        int quantity,
        BigDecimal amount,
        Instant occurredAt
) {}
