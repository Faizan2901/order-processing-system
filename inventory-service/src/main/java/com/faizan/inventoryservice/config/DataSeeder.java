package com.faizan.inventoryservice.config;

import com.faizan.inventoryservice.entity.Inventory;
import com.faizan.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final InventoryRepository inventoryRepository;

    @Bean
    ApplicationRunner seedInventory() {
        return args -> {
            if (inventoryRepository.count() == 0) {
                inventoryRepository.save(new Inventory("P-100", 50));
                inventoryRepository.save(new Inventory("P-200", 10));
                inventoryRepository.save(new Inventory("P-300", 0));   // always out of stock - for testing failures
                log.info("Seeded inventory: P-100=50, P-200=10, P-300=0");
            }
        };
    }
}
