package com.smartperishable.service;

import com.smartperishable.model.*;
import com.smartperishable.model.InventoryBatch.BatchStatus;
import com.smartperishable.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // ===== PRODUCT OPERATIONS =====

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // ===== BATCH OPERATIONS =====

    public List<InventoryBatch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Optional<InventoryBatch> getBatchById(Long id) {
        return batchRepository.findById(id);
    }

    public InventoryBatch addBatch(InventoryBatch batch) {
        InventoryBatch saved = batchRepository.save(batch);
        // Log transaction
        Transaction tx = new Transaction(saved, Transaction.TransactionType.ADDED, saved.getQuantity(), "Batch added to inventory", "system");
        transactionRepository.save(tx);
        return saved;
    }

    public Optional<InventoryBatch> findByRfidTag(String rfidTag) {
        return batchRepository.findByRfidTag(rfidTag);
    }

    // FIFO: Get batches sorted by expiry date (oldest first)
    public List<InventoryBatch> getFifoBatches(Long productId) {
        return batchRepository.findFifoBatches(productId);
    }

    // Sell stock using FIFO
    public Map<String, Object> sellStock(Long productId, int quantity, String performedBy) {
        List<InventoryBatch> fifoBatches = batchRepository.findFifoBatches(productId);
        int remaining = quantity;
        List<String> soldFrom = new ArrayList<>();

        for (InventoryBatch batch : fifoBatches) {
            if (remaining <= 0) break;

            int available = batch.getQuantity();
            int toSell = Math.min(available, remaining);

            batch.setQuantity(available - toSell);
            if (batch.getQuantity() == 0) {
                batch.setStatus(BatchStatus.SOLD_OUT);
            }
            batchRepository.save(batch);

            Transaction tx = new Transaction(batch, Transaction.TransactionType.SOLD, toSell, "FIFO sale", performedBy);
            transactionRepository.save(tx);

            soldFrom.add("Batch #" + batch.getBatchNumber() + " → " + toSell + " units");
            remaining -= toSell;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", remaining == 0);
        result.put("quantitySold", quantity - remaining);
        result.put("details", soldFrom);
        if (remaining > 0) {
            result.put("shortfall", remaining);
        }
        return result;
    }

    // Discard a batch
    public InventoryBatch discardBatch(Long batchId, String reason, String performedBy) {
        InventoryBatch batch = batchRepository.findById(batchId).orElseThrow();
        Transaction tx = new Transaction(batch, Transaction.TransactionType.DISCARDED, batch.getQuantity(), reason, performedBy);
        transactionRepository.save(tx);

        batch.setStatus(BatchStatus.DISCARDED);
        batch.setQuantity(0);
        return batchRepository.save(batch);
    }

    // ===== EXPIRY TRACKING =====

    public List<InventoryBatch> getNearExpiryBatches() {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(7);
        return batchRepository.findNearExpiryBatches(today, threshold);
    }

    public List<InventoryBatch> getExpiredBatches() {
        return batchRepository.findExpiredBatches(LocalDate.now());
    }

    // ===== DEADSTOCK =====

    public List<InventoryBatch> getDeadstock() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        return batchRepository.findDeadstock(cutoff);
    }

    // ===== DASHBOARD STATS =====

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("totalInventory", batchRepository.getTotalActiveInventory());
        stats.put("nearExpiryCount", getNearExpiryBatches().size());
        stats.put("expiredCount", getExpiredBatches().size());
        stats.put("activeBatches", batchRepository.countByStatus(BatchStatus.ACTIVE));
        stats.put("deadstockCount", getDeadstock().size());
        stats.put("totalWastage", transactionRepository.getTotalWastage());
        stats.put("totalSold", transactionRepository.getTotalByType(Transaction.TransactionType.SOLD));
        stats.put("unreadAlerts", alertRepository.countByIsReadFalse());
        return stats;
    }

    // ===== SCHEDULED EXPIRY CHECK (runs every hour) =====

    @Scheduled(fixedRate = 3600000) // Every hour
    public void checkExpiryAndGenerateAlerts() {
        // Check near-expiry
        List<InventoryBatch> nearExpiry = getNearExpiryBatches();
        for (InventoryBatch batch : nearExpiry) {
            if (batch.getStatus() != BatchStatus.NEAR_EXPIRY) {
                batch.setStatus(BatchStatus.NEAR_EXPIRY);
                batchRepository.save(batch);

                Alert alert = new Alert(
                    Alert.AlertType.NEAR_EXPIRY,
                    batch.getProduct().getName() + " (Batch: " + batch.getBatchNumber() + ") expires on " + batch.getExpiryDate() + ". " + batch.getDaysUntilExpiry() + " days remaining.",
                    batch.getProduct().getName(),
                    batch.getId(),
                    Alert.Severity.HIGH
                );
                alertRepository.save(alert);
            }
        }

        // Check expired
        List<InventoryBatch> expired = getExpiredBatches();
        for (InventoryBatch batch : expired) {
            if (batch.getStatus() != BatchStatus.EXPIRED) {
                batch.setStatus(BatchStatus.EXPIRED);
                batchRepository.save(batch);

                Alert alert = new Alert(
                    Alert.AlertType.EXPIRED,
                    batch.getProduct().getName() + " (Batch: " + batch.getBatchNumber() + ") has EXPIRED. Immediate action required.",
                    batch.getProduct().getName(),
                    batch.getId(),
                    Alert.Severity.CRITICAL
                );
                alertRepository.save(alert);
            }
        }

        // Check deadstock
        List<InventoryBatch> deadstock = getDeadstock();
        for (InventoryBatch batch : deadstock) {
            Alert alert = new Alert(
                Alert.AlertType.DEADSTOCK,
                batch.getProduct().getName() + " (Batch: " + batch.getBatchNumber() + ") hasn't moved in 30+ days. Consider discounting.",
                batch.getProduct().getName(),
                batch.getId(),
                Alert.Severity.MEDIUM
            );
            alertRepository.save(alert);
        }

        // Check low stock
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            List<InventoryBatch> activeBatches = batchRepository.findFifoBatches(product.getId());
            int totalStock = activeBatches.stream().mapToInt(InventoryBatch::getQuantity).sum();
            if (totalStock > 0 && totalStock <= product.getMinStockLevel()) {
                Alert alert = new Alert(
                    Alert.AlertType.LOW_STOCK,
                    product.getName() + " stock is low (" + totalStock + " remaining). Minimum level: " + product.getMinStockLevel(),
                    product.getName(),
                    null,
                    Alert.Severity.HIGH
                );
                alertRepository.save(alert);
            }
        }
    }

    // ===== REPORTS =====

    public Map<String, Object> getWastageReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("discardedBatches", batchRepository.findDiscardedBatches());
        report.put("totalWastage", transactionRepository.getTotalWastage());
        report.put("expiredItems", getExpiredBatches());
        return report;
    }

    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findTop50ByOrderByCreatedAtDesc();
    }

    // ===== BIN LOCATION =====

    public List<InventoryBatch> searchByBinLocation(String location) {
        return batchRepository.findByBinLocationContainingIgnoreCase(location);
    }
}
