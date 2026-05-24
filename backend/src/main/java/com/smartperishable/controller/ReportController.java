package com.smartperishable.controller;

import com.smartperishable.model.Transaction;
import com.smartperishable.service.InventoryService;
import com.smartperishable.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/wastage")
    public ResponseEntity<Map<String, Object>> getWastageReport() {
        return ResponseEntity.ok(inventoryService.getWastageReport());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(inventoryService.getRecentTransactions());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = inventoryService.getDashboardStats();
        summary.put("totalAdded", transactionRepository.getTotalByType(Transaction.TransactionType.ADDED));
        summary.put("totalDiscarded", transactionRepository.getTotalByType(Transaction.TransactionType.DISCARDED));
        return ResponseEntity.ok(summary);
    }
}
