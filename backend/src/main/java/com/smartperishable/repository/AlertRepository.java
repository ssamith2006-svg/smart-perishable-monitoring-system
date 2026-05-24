package com.smartperishable.repository;

import com.smartperishable.model.Alert;
import com.smartperishable.model.Alert.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByIsReadFalseOrderByCreatedAtDesc();
    List<Alert> findByAlertTypeOrderByCreatedAtDesc(AlertType alertType);
    List<Alert> findTop20ByOrderByCreatedAtDesc();
    long countByIsReadFalse();
}
