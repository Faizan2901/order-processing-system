package com.faizan.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String recipient;

    @Column(length = 500)
    private String message;

    private Instant sentAt;

    public Notification(Long orderId, String recipient, String message) {
        this.orderId = orderId;
        this.recipient = recipient;
        this.message = message;
        this.sentAt = Instant.now();
    }
}
