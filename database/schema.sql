-- =====================================================
-- Smart Perishable Monitoring System - Database Schema
-- MySQL Database Setup Script
-- =====================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS smart_perishable_db;
USE smart_perishable_db;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'STAFF') NOT NULL DEFAULT 'STAFF',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- PRODUCTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    barcode VARCHAR(50) UNIQUE,
    base_price DECIMAL(10,2),
    unit VARCHAR(50),
    description VARCHAR(500),
    is_perishable BOOLEAN DEFAULT TRUE,
    min_stock_level INT DEFAULT 10,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- INVENTORY BATCHES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS inventory_batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    batch_number VARCHAR(50),
    quantity INT NOT NULL,
    initial_quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    manufacture_date DATE,
    rfid_tag VARCHAR(100) UNIQUE,
    bin_location VARCHAR(100),
    status ENUM('ACTIVE', 'NEAR_EXPIRY', 'EXPIRED', 'DISCARDED', 'SOLD_OUT') DEFAULT 'ACTIVE',
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- ALERTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type ENUM('NEAR_EXPIRY', 'EXPIRED', 'LOW_STOCK', 'DEADSTOCK', 'SYSTEM') NOT NULL,
    message VARCHAR(500) NOT NULL,
    product_name VARCHAR(200),
    batch_id BIGINT,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- TRANSACTIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    transaction_type ENUM('ADDED', 'SOLD', 'DISCARDED', 'RETURNED', 'ADJUSTED') NOT NULL,
    quantity INT NOT NULL,
    notes VARCHAR(500),
    performed_by VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES inventory_batches(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX idx_batch_expiry ON inventory_batches(expiry_date);
CREATE INDEX idx_batch_status ON inventory_batches(status);
CREATE INDEX idx_batch_rfid ON inventory_batches(rfid_tag);
CREATE INDEX idx_batch_bin ON inventory_batches(bin_location);
CREATE INDEX idx_alert_type ON alerts(alert_type);
CREATE INDEX idx_alert_read ON alerts(is_read);
CREATE INDEX idx_tx_type ON transactions(transaction_type);
CREATE INDEX idx_tx_date ON transactions(created_at);

-- =====================================================
-- NOTE: Sample data is auto-seeded by the Spring Boot
-- DataInitializer.java when the application starts.
-- You do NOT need to manually insert data.
-- =====================================================
