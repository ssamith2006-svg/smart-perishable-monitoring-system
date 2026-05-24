package com.smartperishable.config;

import com.smartperishable.model.*;
import com.smartperishable.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private BatchRepository batchRepository;

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (userRepository.count() > 0) return;

        // === USERS ===
        userRepository.save(new User("admin", "admin123", "System Administrator", "admin@supermarket.com", User.Role.ADMIN));
        userRepository.save(new User("staff1", "staff123", "John Smith", "john@supermarket.com", User.Role.STAFF));
        userRepository.save(new User("staff2", "staff123", "Jane Doe", "jane@supermarket.com", User.Role.STAFF));

        // === PRODUCTS ===
        Product milk = productRepository.save(new Product("Fresh Whole Milk", "Dairy", "BAR001", new BigDecimal("3.49"), "litre"));
        Product yogurt = productRepository.save(new Product("Greek Yogurt", "Dairy", "BAR002", new BigDecimal("5.99"), "piece"));
        Product chicken = productRepository.save(new Product("Chicken Breast", "Meat", "BAR003", new BigDecimal("8.99"), "kg"));
        Product salmon = productRepository.save(new Product("Atlantic Salmon Fillet", "Seafood", "BAR004", new BigDecimal("12.99"), "kg"));
        Product bread = productRepository.save(new Product("Whole Wheat Bread", "Bakery", "BAR005", new BigDecimal("2.99"), "piece"));
        Product apple = productRepository.save(new Product("Organic Apples", "Fruits", "BAR006", new BigDecimal("4.49"), "kg"));
        Product lettuce = productRepository.save(new Product("Iceberg Lettuce", "Vegetables", "BAR007", new BigDecimal("1.99"), "piece"));
        Product cheese = productRepository.save(new Product("Cheddar Cheese Block", "Dairy", "BAR008", new BigDecimal("6.49"), "piece"));
        Product juice = productRepository.save(new Product("Orange Juice Fresh", "Beverages", "BAR009", new BigDecimal("3.99"), "litre"));
        Product eggs = productRepository.save(new Product("Free Range Eggs (12)", "Dairy", "BAR010", new BigDecimal("4.99"), "piece"));

        // === INVENTORY BATCHES (with varying expiry dates for demo) ===
        LocalDate today = LocalDate.now();

        // Milk - some near expiry, some fresh
        batchRepository.save(new InventoryBatch(milk, "MLK-001", 50, today.plusDays(2), "RFID-MLK-001", "Aisle-1/Shelf-A/Bin-01"));
        batchRepository.save(new InventoryBatch(milk, "MLK-002", 80, today.plusDays(10), "RFID-MLK-002", "Aisle-1/Shelf-A/Bin-02"));

        // Yogurt - one expired
        batchRepository.save(new InventoryBatch(yogurt, "YGT-001", 30, today.minusDays(2), "RFID-YGT-001", "Aisle-1/Shelf-B/Bin-01"));
        batchRepository.save(new InventoryBatch(yogurt, "YGT-002", 45, today.plusDays(15), "RFID-YGT-002", "Aisle-1/Shelf-B/Bin-02"));

        // Chicken - near expiry
        batchRepository.save(new InventoryBatch(chicken, "CHK-001", 25, today.plusDays(3), "RFID-CHK-001", "Aisle-2/Cold-A/Bin-01"));
        batchRepository.save(new InventoryBatch(chicken, "CHK-002", 40, today.plusDays(8), "RFID-CHK-002", "Aisle-2/Cold-A/Bin-02"));

        // Salmon - expired
        batchRepository.save(new InventoryBatch(salmon, "SAL-001", 15, today.minusDays(1), "RFID-SAL-001", "Aisle-2/Cold-B/Bin-01"));
        batchRepository.save(new InventoryBatch(salmon, "SAL-002", 20, today.plusDays(5), "RFID-SAL-002", "Aisle-2/Cold-B/Bin-02"));

        // Bread - near expiry
        batchRepository.save(new InventoryBatch(bread, "BRD-001", 60, today.plusDays(1), "RFID-BRD-001", "Aisle-3/Shelf-A/Bin-01"));
        batchRepository.save(new InventoryBatch(bread, "BRD-002", 100, today.plusDays(6), "RFID-BRD-002", "Aisle-3/Shelf-A/Bin-02"));

        // Apples - fresh
        batchRepository.save(new InventoryBatch(apple, "APL-001", 200, today.plusDays(20), "RFID-APL-001", "Aisle-4/Shelf-A/Bin-01"));

        // Lettuce - near expiry
        batchRepository.save(new InventoryBatch(lettuce, "LET-001", 35, today.plusDays(4), "RFID-LET-001", "Aisle-4/Shelf-B/Bin-01"));

        // Cheese - good shelf life
        batchRepository.save(new InventoryBatch(cheese, "CHS-001", 70, today.plusDays(45), "RFID-CHS-001", "Aisle-1/Shelf-C/Bin-01"));

        // Juice - expired
        batchRepository.save(new InventoryBatch(juice, "JCE-001", 25, today.minusDays(3), "RFID-JCE-001", "Aisle-5/Shelf-A/Bin-01"));
        batchRepository.save(new InventoryBatch(juice, "JCE-002", 40, today.plusDays(12), "RFID-JCE-002", "Aisle-5/Shelf-A/Bin-02"));

        // Eggs - fresh
        batchRepository.save(new InventoryBatch(eggs, "EGG-001", 90, today.plusDays(25), "RFID-EGG-001", "Aisle-1/Shelf-D/Bin-01"));

        System.out.println("✅ Sample data initialized successfully!");
    }
}
