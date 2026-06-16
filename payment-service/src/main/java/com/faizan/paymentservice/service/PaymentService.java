package com.faizan.paymentservice.service;

import com.faizan.common.event.InventoryReservedEvent;
import com.faizan.common.event.OrderFailedEvent;
import com.faizan.common.event.OrderPaidEvent;
import com.faizan.paymentservice.entity.Payment;
import com.faizan.paymentservice.entity.PaymentStatus;
import com.faizan.paymentservice.entity.ProcessedEvent;
import com.faizan.paymentservice.event.PaymentEventProducer;
import com.faizan.paymentservice.repository.PaymentRepository;
import com.faizan.paymentservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final PaymentEventProducer producer;

    // Simple rule to simulate declines: amounts over 1000 are "declined".
    private static final BigDecimal DECLINE_THRESHOLD = new BigDecimal("1000");

    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate event ignored: eventId={}", event.eventId());
            return;
        }

        boolean approved = event.amount().compareTo(DECLINE_THRESHOLD) <= 0;

        if (approved) {
            Payment payment = new Payment(event.orderId(), event.amount(), PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            log.info("Payment SUCCESS for orderId={}, amount={}", event.orderId(), event.amount());

            producer.publishPaid(new OrderPaidEvent(
                    UUID.randomUUID().toString(),
                    event.orderId(),
                    event.customerEmail(),
                    "PAY-" + payment.getId(),
                    Instant.now()
            ));
        } else {
            paymentRepository.save(new Payment(event.orderId(), event.amount(), PaymentStatus.DECLINED));
            log.info("Payment DECLINED for orderId={}, amount={}", event.orderId(), event.amount());

            // This triggers the saga: Inventory will release the reserved stock.
            producer.publishFailed(new OrderFailedEvent(
                    UUID.randomUUID().toString(),
                    event.orderId(),
                    event.customerEmail(),
                    event.productId(),
                    event.quantity(),
                    "PAYMENT_DECLINED",
                    Instant.now()
            ));
        }

        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}
