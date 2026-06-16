package com.faizan.paymentservice.event;

import com.faizan.common.event.InventoryReservedEvent;
import com.faizan.common.event.Topics;
import com.faizan.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "payment-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.InventoryReservedEvent"})
    public void onInventoryReserved(InventoryReservedEvent event, Acknowledgment ack) {
        log.info("Received inventory-reserved: orderId={}", event.orderId());
        paymentService.handleInventoryReserved(event);
        ack.acknowledge();
    }
}
