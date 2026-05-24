package com.smartperishable.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "inventory_batches")
public class InventoryBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "rfid_tag", length = 100, unique = true)
    private String rfidTag;

    @Column(name = "bin_location", length = 100)
    private String binLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BatchStatus status = BatchStatus.ACTIVE;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BatchStatus {
        ACTIVE, NEAR_EXPIRY, EXPIRED, DISCARDED, SOLD_OUT
    }

    @PrePersist
    protected void onCreate() {
        this.addedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.initialQuantity == null) {
            this.initialQuantity = this.quantity;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Calculated Fields
    @Transient
    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.expiryDate);
    }

    @Transient
    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expiryDate);
    }

    @Transient
    public boolean isNearExpiry() {
        long days = getDaysUntilExpiry();
        return days >= 0 && days <= 7;
    }

    // Constructors
    public InventoryBatch() {}

    public InventoryBatch(Product product, String batchNumber, Integer quantity,
                          LocalDate expiryDate, String rfidTag, String binLocation) {
        this.product = product;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.initialQuantity = quantity;
        this.expiryDate = expiryDate;
        this.rfidTag = rfidTag;
        this.binLocation = binLocation;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public String getRfidTag() { return rfidTag; }
    public void setRfidTag(String rfidTag) { this.rfidTag = rfidTag; }

    public String getBinLocation() { return binLocation; }
    public void setBinLocation(String binLocation) { this.binLocation = binLocation; }

    public BatchStatus getStatus() { return status; }
    public void setStatus(BatchStatus status) { this.status = status; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
