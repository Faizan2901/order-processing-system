package com.faizan.orderservice.event;

import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.OrderPaidEvent;
import com.faizan.common.event.Topics;
import com.faizan.orderservice.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderResultConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = Topics.ORDER_PAID, groupId = "order-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderPaidEvent"})
    public void onOrderPaid(OrderPaidEvent event, Acknowledgment ack) {
        log.info("Received order-paid: orderId={}", event.orderId());
        orderStatusService.markConfirmed(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = Topics.ORDER_FAILED, groupId = "order-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderFailedEvent"})
    public void onOrderFailed(OrderFailedEvent event, Acknowledgment ack) {
        log.info("Received order-failed: orderId={}", event.orderId());
        orderStatusService.markFailed(event);
        ack.acknowledge();
    }
}
