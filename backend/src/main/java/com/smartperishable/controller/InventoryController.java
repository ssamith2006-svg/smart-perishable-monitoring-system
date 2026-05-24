package com.smartperishable.controller;

import com.smartperishable.model.InventoryBatch;
import com.smartperishable.model.Product;
import com.smartperishable.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/batches")
    public ResponseEntity<List<InventoryBatch>> getAllBatches() {
        return ResponseEntity.ok(inventoryService.getAllBatches());
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<InventoryBatch> getBatch(@PathVariable Long id) {
        return inventoryService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batches")
    public ResponseEntity<?> addBatch(@RequestBody Map<String, Object> request) {
        try {
            Long productId = Long.valueOf(request.get("productId").toString());
            Product product = inventoryService.getProductById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            InventoryBatch batch = new InventoryBatch();
            batch.setProduct(product);
            batch.setBatchNumber(request.getOrDefault("batchNumber", "B-" + System.currentTimeMillis()).toString());
            batch.setQuantity(Integer.parseInt(request.get("quantity").toString()));
            batch.setInitialQuantity(batch.getQuantity());
            batch.setExpiryDate(LocalDate.parse(request.get("expiryDate").toString()));
            batch.setRfidTag(request.getOrDefault("rfidTag", "RFID-" + System.currentTimeMillis()).toString());
            batch.setBinLocation(request.getOrDefault("binLocation", "Unassigned").toString());

            if (request.containsKey("manufactureDate") && request.get("manufactureDate") != null) {
                batch.setManufactureDate(LocalDate.parse(request.get("manufactureDate").toString()));
            }

            InventoryBatch saved = inventoryService.addBatch(batch);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody Map<String, Object> request) {
        Long productId = Long.valueOf(request.get("productId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        String performedBy = request.getOrDefault("performedBy", "staff").toString();
        return ResponseEntity.ok(inventoryService.sellStock(productId, quantity, performedBy));
    }

    @PostMapping("/discard/{batchId}")
    public ResponseEntity<?> discardBatch(@PathVariable Long batchId, @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "Expired");
        String performedBy = request.getOrDefault("performedBy", "staff");
        return ResponseEntity.ok(inventoryService.discardBatch(batchId, reason, performedBy));
    }

    @GetMapping("/near-expiry")
    public ResponseEntity<List<InventoryBatch>> getNearExpiry() {
        return ResponseEntity.ok(inventoryService.getNearExpiryBatches());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<InventoryBatch>> getExpired() {
        return ResponseEntity.ok(inventoryService.getExpiredBatches());
    }

    @GetMapping("/deadstock")
    public ResponseEntity<List<InventoryBatch>> getDeadstock() {
        return ResponseEntity.ok(inventoryService.getDeadstock());
    }

    @GetMapping("/fifo/{productId}")
    public ResponseEntity<List<InventoryBatch>> getFifo(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getFifoBatches(productId));
    }

    @GetMapping("/search/rfid/{rfidTag}")
    public ResponseEntity<?> searchByRfid(@PathVariable String rfidTag) {
        return inventoryService.findByRfidTag(rfidTag)
                .map(b -> ResponseEntity.ok((Object) b))
                .orElse(ResponseEntity.ok(Map.of("found", false, "message", "RFID tag not found")));
    }

    @GetMapping("/search/bin/{location}")
    public ResponseEntity<List<InventoryBatch>> searchByBin(@PathVariable String location) {
        return ResponseEntity.ok(inventoryService.searchByBinLocation(location));
    }
}
