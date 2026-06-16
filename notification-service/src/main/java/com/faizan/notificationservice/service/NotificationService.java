package com.faizan.notificationservice.service;

import com.faizan.notificationservice.entity.Notification;
import com.faizan.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void send(Long orderId, String recipient, String message) {
        notificationRepository.save(new Notification(orderId, recipient, message));
        // In production this would call an email/SMS provider. Here we log it.
        log.info("NOTIFICATION sent to {} | orderId={} | {}", recipient, orderId, message);
    }
}
