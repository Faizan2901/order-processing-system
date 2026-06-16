package com.faizan.notificationservice.event;

import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.OrderPaidEvent;
import com.faizan.common.event.Topics;
import com.faizan.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = Topics.ORDER_PAID, groupId = "notification-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderPaidEvent"})
    public void onOrderPaid(OrderPaidEvent event, Acknowledgment ack) {
        notificationService.send(event.orderId(), event.customerEmail(),
                "Your order #" + event.orderId() + " is confirmed. Payment ref: " + event.paymentId());
        ack.acknowledge();
    }

    @KafkaListener(topics = Topics.ORDER_FAILED, groupId = "notification-service-group",
            properties = {"spring.json.value.default.type=com.faizan.common.event.OrderFailedEvent"})
    public void onOrderFailed(OrderFailedEvent event, Acknowledgment ack) {
        notificationService.send(event.orderId(), event.customerEmail(),
                "Your order #" + event.orderId() + " could not be processed. Reason: " + event.reason());
        ack.acknowledge();
    }
}
