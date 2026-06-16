package com.faizan.paymentservice.event;

import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.OrderPaidEvent;
import com.faizan.common.event.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaid(OrderPaidEvent event) {
        kafkaTemplate.send(Topics.ORDER_PAID, String.valueOf(event.orderId()), event);
        log.info("Published order-paid: orderId={}", event.orderId());
    }

    public void publishFailed(OrderFailedEvent event) {
        kafkaTemplate.send(Topics.ORDER_FAILED, String.valueOf(event.orderId()), event);
        log.info("Published order-failed: orderId={}, reason={}", event.orderId(), event.reason());
    }
}
