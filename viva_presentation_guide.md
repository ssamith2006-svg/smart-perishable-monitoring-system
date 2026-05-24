# 🎓 Viva & Demo Presentation Guide
**Smart Perishable Monitoring System for Supermarkets**

This guide provides a structured flow for demonstrating your project to your professors/evaluators and includes potential Viva questions and answers to help you prepare.

---

## 🎬 Part 1: Step-by-Step Demo Script

When asked to "Show us your project," follow this exact sequence to highlight the best features of your system.

### 1. The Introduction (Login Page)
* **Action:** Open `frontend/login.html` in your browser.
* **Talking Point:** *"Welcome to the Smart Perishable Monitoring System. This system is designed to solve a major problem in large supermarkets: tracking the exact expiry dates and physical bin locations of perishable goods to reduce wastage and deadstock."*
* **Action:** Log in using the admin credentials (`admin` / `admin123`).

### 2. The Dashboard (High-Level Overview)
* **Action:** Show the main Dashboard.
* **Talking Point:** *"Upon logging in, the Admin sees a real-time statistical overview. You can see the total active inventory, units sold, and critical metrics like 'Near Expiry' or 'Expired' items. The charts dynamically break down our inventory status and sales-to-wastage ratio."*
* **Highlight:** Point out the colored stat cards and explain how they give an immediate health check of the supermarket's stock.

### 3. Inventory & FIFO Selling (Core Logic)
* **Action:** Navigate to the **Inventory** tab.
* **Talking Point:** *"This is the core of the system. Notice how each batch has an exact 'Days Left' calculation. Items nearing expiry are highlighted automatically."*
* **Action:** Click **"Sell (FIFO)"**, select a product (e.g., Milk), and enter a quantity to sell.
* **Talking Point:** *"When an item is sold, the system automatically uses First-In-First-Out (FIFO) logic. It finds the oldest batches nearest to expiry and deducts from them first, ensuring the supermarket doesn't lose money on expired goods."*

### 4. Hardware Simulation (Scanner Module)
* **Action:** Navigate to the **Scanner** tab.
* **Talking Point:** *"In a real-world scenario, supermarket staff use handheld scanners. Here, we simulate that. By entering an RFID tag or Barcode, the system instantly fetches the item's details, including its exact physical Bin Location (e.g., Aisle 1, Shelf A), making it easy to locate."*
* **Action:** Enter a barcode (e.g., `BAR001`) and hit Scan to show the result.

### 5. Automation & Alerts
* **Action:** Navigate to the **Alerts** tab. Click **"Run Check"** (⚡ icon at the top).
* **Talking Point:** *"The Spring Boot backend has a scheduled automated job that runs in the background every hour. It checks the dates of every single item. If an item becomes 'Near Expiry' or sits as 'Deadstock' for over 30 days, it automatically triggers an alert here for the staff to take action (like applying a discount)."*

### 6. Analytics & Reports
* **Action:** Navigate to the **Reports** tab.
* **Talking Point:** *"Finally, we have comprehensive analytics. The system logs every transaction (Added, Sold, Discarded) and generates a Wastage Report. This allows store managers to see exactly how much revenue was lost to expired items versus actual sales."*

---

## 🤔 Part 2: Expected Viva Questions & Answers

Be prepared to answer these technical and conceptual questions from your evaluators.

### Q1: What makes this system different from a standard Supermarket Billing System?
**A:** Standard billing systems track *total quantity* (e.g., 50 cartons of milk). They don't know *which* carton expires *when*. My system tracks inventory at the **Batch Level**. Every batch has an RFID tag, bin location, and an explicit expiry date, allowing for automated FIFO selling and expiry alerts.

### Q2: How did you implement the FIFO (First-In-First-Out) logic?
**A:** In the Spring Boot `InventoryService`, when a sale is requested, the system queries the database for all active batches of that product, **sorted by expiry date in ascending order**. It deducts the requested quantity from the oldest batch first. If the oldest batch doesn't have enough quantity, it empties that batch and deducts the remainder from the next oldest batch.

### Q3: How do the automated alerts work? Does someone have to click a button?
**A:** No, it is fully automated. I used Spring Boot's `@Scheduled` annotation on a background service method. It runs automatically at a fixed interval (e.g., every hour). It checks the current date against the `expiry_date` of all active batches in the MySQL database and generates alert records if thresholds are met.

### Q4: Why did you choose Java Spring Boot and MySQL for the backend?
**A:** Spring Boot provides a robust, enterprise-grade framework for building REST APIs with built-in dependency injection and security. MySQL is a relational database perfectly suited for this project because the data is highly structured (Products have a 1-to-Many relationship with Batches, and Batches have a 1-to-Many relationship with Transactions). 

### Q5: How do the frontend and backend communicate?
**A:** The frontend is a Single Page Application (SPA) built with pure HTML/JS that communicates with the Spring Boot backend asynchronously using the JavaScript `fetch()` API. It sends and receives data in JSON format via RESTful endpoints (e.g., `GET /api/inventory/batches`).

### Q6: Can this be deployed to the cloud?
**A:** Yes, the entire project is Dockerized. I created a `Dockerfile` for the Spring Boot backend and a `docker-compose.yml` file that orchestrates both the database and backend. It can be easily deployed to AWS, Azure, or any cloud provider running Docker.

---

## 💡 Quick Tips for the Presentation
* **Speak confidently:** You built this! Own the code.
* **Don't rush:** Let them see the UI. It looks premium and modern; let them appreciate the design.
* **Focus on the business value:** Professors love when a project solves a *real-world* problem (reducing food waste and saving money).
