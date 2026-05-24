package com.smartperishable.repository;

import com.smartperishable.model.InventoryBatch;
import com.smartperishable.model.InventoryBatch.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<InventoryBatch, Long> {

    Optional<InventoryBatch> findByRfidTag(String rfidTag);

    List<InventoryBatch> findByStatus(BatchStatus status);

    List<InventoryBatch> findByProductIdOrderByExpiryDateAsc(Long productId);

    // FIFO: Get oldest active/near-expiry batches first
    @Query("SELECT b FROM InventoryBatch b WHERE b.product.id = :productId AND b.status IN ('ACTIVE', 'NEAR_EXPIRY') AND b.quantity > 0 ORDER BY b.expiryDate ASC")
    List<InventoryBatch> findFifoBatches(@Param("productId") Long productId);

    // Near expiry: expires within N days
    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate BETWEEN :today AND :threshold AND b.status IN ('ACTIVE','NEAR_EXPIRY') AND b.quantity > 0")
    List<InventoryBatch> findNearExpiryBatches(@Param("today") LocalDate today, @Param("threshold") LocalDate threshold);

    // Expired batches
    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate < :today AND b.status NOT IN ('DISCARDED','SOLD_OUT')")
    List<InventoryBatch> findExpiredBatches(@Param("today") LocalDate today);

    // Active batches with stock
    @Query("SELECT b FROM InventoryBatch b WHERE b.status = 'ACTIVE' AND b.quantity > 0")
    List<InventoryBatch> findActiveBatches();

    // Total inventory count
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM InventoryBatch b WHERE b.status IN ('ACTIVE','NEAR_EXPIRY')")
    Long getTotalActiveInventory();

    // Deadstock: items not sold for a long time (quantity hasn't changed)
    @Query("SELECT b FROM InventoryBatch b WHERE b.status = 'ACTIVE' AND b.quantity = b.initialQuantity AND b.addedAt < :cutoffDate")
    List<InventoryBatch> findDeadstock(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);

    // Search by bin location
    List<InventoryBatch> findByBinLocationContainingIgnoreCase(String binLocation);

    // Wastage: discarded batches
    @Query("SELECT b FROM InventoryBatch b WHERE b.status = 'DISCARDED'")
    List<InventoryBatch> findDiscardedBatches();

    // Count by status
    long countByStatus(BatchStatus status);
}
