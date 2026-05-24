# 🥬 Smart Perishable Monitoring System

A comprehensive web-based monitoring system designed for large-scale supermarkets to track perishable inventory, manage expiry dates, and optimize stock management using RFID-based bin location tracking.

## 📋 Project Description

Existing supermarket invoicing systems lack provision for expiry date tracking of perishable items, leading to deadstock build-up and revenue loss. This Smart Perishable Monitoring System tracks expiration dates alongside RFID sticker-based bin locations to help optimize loss, resulting in better management and profitability.

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 21 + Spring Boot 3.4.4 |
| **Database** | MySQL 8.0+ |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap 5 |
| **Charts** | Chart.js 4.x |
| **Build** | Apache Maven |

## ✨ Key Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | 📅 Expiry Date Tracking | Track expiry dates, auto-calculate days remaining, highlight near-expiry/expired |
| 2 | 📡 RFID Bin Location | RFID tag assignment, bin location tracking, quick item locator |
| 3 | 🔔 Smart Alerts | Auto-alerts for near-expiry, expired, low stock, deadstock |
| 4 | 📦 Automated Inventory | Real-time stock tracking with add/sell/discard operations |
| 5 | ⚠️ Deadstock Detection | Identify slow-moving products, suggest discounts |
| 6 | 🔄 FIFO Support | First-In-First-Out selling, oldest stock sold first |
| 7 | 📊 Real-Time Dashboard | Live stats, charts, inventory overview |
| 8 | 📷 Barcode/RFID Scanning | Simulate barcode & RFID scanning for fast data entry |
| 9 | 📈 Reports & Analytics | Wastage, sales vs loss, stock usage reports |
| 10 | 👤 User Management | Admin/Staff roles with role-based access control |

## 📁 Project Structure

```
smart-perishable-monitoring-system/
├── backend/                          # Spring Boot Backend
│   ├── pom.xml                       # Maven dependencies
│   └── src/main/java/com/smartperishable/
│       ├── SmartMonitoringApplication.java
│       ├── config/
│       │   ├── WebConfig.java        # CORS configuration
│       │   └── DataInitializer.java  # Sample data seeder
│       ├── model/
│       │   ├── User.java
│       │   ├── Product.java
│       │   ├── InventoryBatch.java
│       │   ├── Alert.java
│       │   └── Transaction.java
│       ├── repository/
│       │   ├── UserRepository.java
│       │   ├── ProductRepository.java
│       │   ├── BatchRepository.java
│       │   ├── AlertRepository.java
│       │   └── TransactionRepository.java
│       ├── service/
│       │   └── InventoryService.java
│       └── controller/
│           ├── AuthController.java
│           ├── ProductController.java
│           ├── InventoryController.java
│           ├── DashboardController.java
│           ├── AlertController.java
│           └── ReportController.java
├── frontend/                         # HTML/CSS/JS Frontend
│   ├── css/style.css                # Premium dark theme CSS
│   ├── login.html                   # Login page
│   ├── dashboard.html               # Main dashboard
│   ├── inventory.html               # Inventory management
│   ├── scanner.html                 # RFID/Barcode scanner
│   ├── reports.html                 # Reports & analytics
│   ├── alerts.html                  # Alerts center
│   └── users.html                   # User management
├── database/
│   └── schema.sql                   # MySQL schema script
├── run_project.bat                  # Windows launcher
└── README.md
```

## 🚀 How to Run

### Prerequisites
- **Java 21+** installed ([Download](https://adoptium.net))
- **Maven 3.8+** installed ([Download](https://maven.apache.org))
- **MySQL 8.0+** running locally ([Download](https://dev.mysql.com/downloads/mysql/))

### Step 1: Setup MySQL Database
```sql
CREATE DATABASE smart_perishable_db;
```
Or run the provided schema script:
```bash
mysql -u root -p < database/schema.sql
```

### Step 2: Configure Database Credentials
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

### Step 3: Build & Run Backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/smart-monitoring-1.0.0.jar
```
The backend will start on **http://localhost:8080**

### Step 4: Open Frontend
Open `frontend/login.html` in your web browser.

### Quick Launch (Windows)
Double-click `run_project.bat` to automatically build and launch everything.

### Docker Deployment
You can easily spin up both the database and the backend using Docker Compose.
```bash
docker-compose up -d --build
```
This will start the MySQL database and the Spring Boot backend on `http://localhost:8080`.

## 🔐 Demo Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Staff | `staff1` | `staff123` |
| Staff | `staff2` | `staff123` |

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/register` | Register new user |
| GET | `/api/products` | List all products |
| POST | `/api/products` | Add new product |
| GET | `/api/products/barcode/{code}` | Find product by barcode |
| GET | `/api/inventory/batches` | List all inventory batches |
| POST | `/api/inventory/batches` | Add new batch |
| POST | `/api/inventory/sell` | Sell stock (FIFO) |
| POST | `/api/inventory/discard/{id}` | Discard a batch |
| GET | `/api/inventory/near-expiry` | Get near-expiry items |
| GET | `/api/inventory/expired` | Get expired items |
| GET | `/api/inventory/deadstock` | Get deadstock items |
| GET | `/api/inventory/fifo/{productId}` | Get FIFO order |
| GET | `/api/inventory/search/rfid/{tag}` | Search by RFID tag |
| GET | `/api/inventory/search/bin/{loc}` | Search by bin location |
| GET | `/api/dashboard/stats` | Dashboard statistics |
| POST | `/api/dashboard/check-expiry` | Trigger expiry check |
| GET | `/api/alerts` | Get all alerts |
| PUT | `/api/alerts/{id}/read` | Mark alert as read |
| GET | `/api/reports/wastage` | Wastage report |
| GET | `/api/reports/transactions` | Transaction history |
| GET | `/api/reports/summary` | Full summary report |

## 📚 API Documentation (Swagger)

Once the backend is running, you can access the full interactive Swagger API Documentation at:
**http://localhost:8080/swagger-ui/index.html**

## 📊 Database Schema (ER Diagram)

```
┌─────────────┐      ┌───────────────────┐      ┌──────────────┐
│   USERS     │      │    PRODUCTS       │      │   ALERTS     │
├─────────────┤      ├───────────────────┤      ├──────────────┤
│ id          │      │ id                │      │ id           │
│ username    │      │ name              │      │ alert_type   │
│ password    │      │ category          │      │ message      │
│ full_name   │      │ barcode           │      │ severity     │
│ email       │      │ base_price        │      │ is_read      │
│ role        │      │ unit              │      │ created_at   │
│ is_active   │      │ min_stock_level   │      └──────────────┘
└─────────────┘      └────────┬──────────┘
                              │ 1:N
                    ┌─────────┴─────────┐
                    │ INVENTORY_BATCHES  │
                    ├───────────────────┤
                    │ id                │
                    │ product_id (FK)   │
                    │ batch_number      │
                    │ quantity          │
                    │ expiry_date       │ ← Key Feature
                    │ rfid_tag          │ ← Key Feature
                    │ bin_location      │ ← Key Feature
                    │ status            │
                    └────────┬──────────┘
                             │ 1:N
                    ┌────────┴──────────┐
                    │  TRANSACTIONS     │
                    ├───────────────────┤
                    │ id                │
                    │ batch_id (FK)     │
                    │ transaction_type  │
                    │ quantity          │
                    │ performed_by      │
                    └───────────────────┘
```

## 🎓 Academic Information

- **Project Title:** Smart Perishable Monitoring System for Large Scale Supermarkets
- **Technologies:** Java Spring Boot, MySQL, HTML/CSS/JS, Bootstrap, Chart.js
- **Type:** Full-Stack Web Application (DBMS Project)
