package com.faizan.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    private String productId;

    @Column(nullable = false)
    private int availableQuantity;

    @Version
    private Long version;   // optimistic locking - prevents lost updates under concurrency

    public Inventory(String productId, int availableQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }
}
