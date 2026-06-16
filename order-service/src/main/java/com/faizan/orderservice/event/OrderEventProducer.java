package com.faizan.orderservice.event;

import com.faizan.common.event.OrderCreatedEvent;
import com.faizan.common.event.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        // Key by orderId so all events for one order land on the same partition (ordering).
        kafkaTemplate.send(Topics.ORDER_CREATED, String.valueOf(event.orderId()), event);
        log.info("Published order-created event: orderId={}, eventId={}",
                event.orderId(), event.eventId());
    }
}
