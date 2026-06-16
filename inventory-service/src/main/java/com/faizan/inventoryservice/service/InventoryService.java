package com.faizan.inventoryservice.service;

import com.faizan.common.event.*;
import com.faizan.inventoryservice.entity.Inventory;
import com.faizan.inventoryservice.entity.ProcessedEvent;
import com.faizan.inventoryservice.event.InventoryEventProducer;
import com.faizan.inventoryservice.repository.InventoryRepository;
import com.faizan.inventoryservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final InventoryEventProducer producer;

    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {

        if ("P-999".equals(event.productId())) {
            throw new RuntimeException("Simulated processing failure for DLQ test");
        }

        // Idempotency guard: skip if we've already processed this event.
        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate event ignored: eventId={}", event.eventId());
            return;
        }

        Inventory inventory = inventoryRepository.findById(event.productId()).orElse(null);

        if (inventory == null || inventory.getAvailableQuantity() < event.quantity()) {
            log.info("Insufficient stock for product={}, orderId={}", event.productId(), event.orderId());
            producer.publishFailed(new OrderFailedEvent(
                    UUID.randomUUID().toString(),
                    event.orderId(),
                    event.customerEmail(),
                    event.productId(),
                    event.quantity(),
                    "OUT_OF_STOCK",
                    Instant.now()
            ));
        } else {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - event.quantity());
            inventoryRepository.save(inventory);
            log.info("Reserved {} units of {} for orderId={}",
                    event.quantity(), event.productId(), event.orderId());

            producer.publishReserved(new InventoryReservedEvent(
                    UUID.randomUUID().toString(),
                    event.orderId(),
                    event.customerEmail(),
                    event.productId(),
                    event.quantity(),
                    event.amount(),
                    Instant.now()
            ));
        }

        // Mark processed inside the same transaction.
        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }

    /** Compensating transaction: release stock when an order fails after reservation. */
    @Transactional
    public void handleOrderFailed(OrderFailedEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            log.warn("Duplicate failed-event ignored: eventId={}", event.eventId());
            return;
        }
        // Only release if this failure came from a later stage (e.g. payment),
        // i.e. stock was actually reserved. Out-of-stock failures originate here.
        if (!"OUT_OF_STOCK".equals(event.reason())) {
            inventoryRepository.findById(event.productId()).ifPresent(inv -> {
                inv.setAvailableQuantity(inv.getAvailableQuantity() + event.quantity());
                inventoryRepository.save(inv);
                log.info("Released {} units of {} back to stock (orderId={})",
                        event.quantity(), event.productId(), event.orderId());
            });
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}
