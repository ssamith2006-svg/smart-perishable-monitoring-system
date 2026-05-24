package com.smartperishable.controller;

import com.smartperishable.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(inventoryService.getDashboardStats());
    }

    @PostMapping("/check-expiry")
    public ResponseEntity<?> triggerExpiryCheck() {
        inventoryService.checkExpiryAndGenerateAlerts();
        return ResponseEntity.ok(Map.of("success", true, "message", "Expiry check completed"));
    }
}
