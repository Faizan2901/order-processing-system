package com.faizan.orderservice.entity;

public enum OrderStatus {
    PENDING,     // created, waiting for inventory + payment
    CONFIRMED,   // payment succeeded
    FAILED       // out of stock or payment declined
}
