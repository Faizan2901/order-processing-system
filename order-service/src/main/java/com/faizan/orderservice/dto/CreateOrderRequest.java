package com.faizan.orderservice.dto;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String customerEmail,
        String productId,
        int quantity,
        BigDecimal amount
) {}
