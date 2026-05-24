package com.smartperishable.controller;

import com.smartperishable.model.Alert;
import com.smartperishable.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public ResponseEntity<List<Alert>> getRecentAlerts() {
        return ResponseEntity.ok(alertRepository.findTop20ByOrderByCreatedAtDesc());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Alert>> getUnreadAlerts() {
        return ResponseEntity.ok(alertRepository.findByIsReadFalseOrderByCreatedAtDesc());
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", alertRepository.countByIsReadFalse()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Alert alert = alertRepository.findById(id).orElseThrow();
        alert.setIsRead(true);
        alertRepository.save(alert);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        List<Alert> unread = alertRepository.findByIsReadFalseOrderByCreatedAtDesc();
        unread.forEach(a -> a.setIsRead(true));
        alertRepository.saveAll(unread);
        return ResponseEntity.ok(Map.of("success", true, "count", unread.size()));
    }
}
