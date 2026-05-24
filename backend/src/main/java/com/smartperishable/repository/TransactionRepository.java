package com.smartperishable.repository;

import com.smartperishable.model.Transaction;
import com.smartperishable.model.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType type);
    List<Transaction> findTop50ByOrderByCreatedAtDesc();

    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transaction t WHERE t.transactionType = :type")
    Long getTotalByType(@Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transaction t WHERE t.transactionType = 'DISCARDED'")
    Long getTotalWastage();
}
