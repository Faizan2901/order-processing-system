package com.faizan.inventoryservice.event;

import com.faizan.common.event.OrderCreatedEvent;
import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.Topics;
import com.faizan.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "inventory-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderCreatedEvent"})
    public void onOrderCreated(OrderCreatedEvent event, Acknowledgment ack) {
        log.info("Received order-created: orderId={}", event.orderId());
        inventoryService.handleOrderCreated(event);
        ack.acknowledge();   // commit offset only after successful processing
    }

    @KafkaListener(topics = Topics.ORDER_FAILED, groupId = "inventory-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderFailedEvent"})
    public void onOrderFailed(OrderFailedEvent event, Acknowledgment ack) {
        log.info("Received order-failed (compensation check): orderId={}", event.orderId());
        inventoryService.handleOrderFailed(event);
        ack.acknowledge();
    }
}
