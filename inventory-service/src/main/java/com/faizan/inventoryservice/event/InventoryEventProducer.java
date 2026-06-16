package com.faizan.inventoryservice.event;

import com.faizan.common.event.InventoryReservedEvent;
import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReserved(InventoryReservedEvent event) {
        kafkaTemplate.send(Topics.INVENTORY_RESERVED, String.valueOf(event.orderId()), event);
        log.info("Published inventory-reserved: orderId={}", event.orderId());
    }

    public void publishFailed(OrderFailedEvent event) {
        kafkaTemplate.send(Topics.ORDER_FAILED, String.valueOf(event.orderId()), event);
        log.info("Published order-failed: orderId={}, reason={}", event.orderId(), event.reason());
    }
}
