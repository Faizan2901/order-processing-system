package com.faizan.common.event;

/**
 * Central place for all Kafka topic names.
 * Using constants avoids typos and keeps producers/consumers in sync.
 */
public final class Topics {

    private Topics() {}

    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String ORDER_PAID = "order-paid";
    public static final String ORDER_FAILED = "order-failed";
    public static final String NOTIFICATIONS = "notifications";
}
