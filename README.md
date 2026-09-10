# Inventory & Stock Management Analytics System

A cloud-based Inventory and Stock Management Analytics System built with Spring Boot, MySQL, and Thymeleaf. Monitors stock levels, identifies fast/slow-moving inventory, and supports inventory-related decision-making.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.1.0 |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Frontend | Thymeleaf, Chart.js, CSS3 |
| Security | Spring Security + JWT |
| Build | Maven |
| Deployment | Docker, Docker Compose |

## Features

### Inventory Management
- Product CRUD with SKU, barcode, GST/HSN support
- Warehouse management with multi-location tracking
- Category and supplier management
- Customer management with GSTIN

### Stock Tracking
- Real-time inventory levels per product per warehouse
- Stock movement audit trail (Purchase, Sale, Damage, Transfer, Adjustment)
- Automatic stock updates on purchases, sales, and invoices

### Analytics Dashboard
- **Dashboard Overview** -- Total stock value, sales, low stock alerts
- **Stock Velocity** -- Fast, Medium, Slow, Dead mover classification
- **ABC Analysis** -- A (top 80% revenue), B (next 15%), C (last 5%)
- **Dead Stock Detection** -- Products with zero sales but existing stock
- **Reorder Suggestions** -- Products below reorder point with suggested quantities
- **Sales Trend** -- Daily, weekly, monthly sales charts
- **Purchase vs Sales** -- Side-by-side comparison
- **Warehouse Stock** -- Per-warehouse stock summary with value breakdown

### GST Compliance
- Auto-calculated CGST/SGST on invoices
- HSN code and GST% per product
- GSTIN on business and customer entities

## Project Structure

```
inventory-erp/
├── src/main/java/com/project/inventoryerp/
│   ├── *Controller.java          # REST + Page controllers
│   ├── *Repository.java          # JPA repositories with custom queries
│   ├── *Service.java             # Business logic
│   ├── *Request.java             # Request DTOs
│   ├── *.java                    # Entity classes
│   ├── SecurityConfig.java       # Spring Security + JWT config
│   ├── JwtUtil.java              # JWT token generation/validation
│   └── JwtAuthFilter.java        # JWT authentication filter
├── src/main/resources/
│   ├── application.properties    # App configuration
│   ├── static/css/style.css      # Frontend styles
│   ├── static/js/app.js          # Frontend API utilities
│   └── templates/                # Thymeleaf HTML pages
├── Dockerfile                    # Multi-stage Docker build
├── docker-compose.yml            # MySQL + App setup
└── pom.xml                       # Maven dependencies
```

## Setup

### Prerequisites
- Java 21+
- MySQL 8.0
- Maven 3.6+

### Option 1: Local Setup

1. Create MySQL database:
```sql
CREATE DATABASE inventory_erp;
```

2. Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_erp
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Open `http://localhost:8080`

### Option 2: Docker

1. Run with Docker Compose:
```bash
docker-compose up --build
```

2. Open `http://localhost:8080`

### First Time Setup
1. Register a new account via `POST /api/auth/register`
2. Login at `/login` with your credentials
3. Start adding products, warehouses, and inventory

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user + business |
| POST | `/api/auth/login` | Login, returns JWT token |

### Reports
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/total-sales?startDate=&endDate=` | Total sales with optional date range |
| GET | `/api/reports/total-purchases?startDate=&endDate=` | Total purchases with optional date range |
| GET | `/api/reports/low-stock?threshold=10&warehouseId=` | Low stock items |
| GET | `/api/reports/product-count` | Total product count |
| GET | `/api/reports/total-stock-value` | Total stock value across warehouses |

### Analytics
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/analytics/velocity?days=30` | Stock velocity (Fast/Medium/Slow/Dead) |
| GET | `/api/analytics/abc-analysis?days=90` | ABC classification |
| GET | `/api/analytics/dead-stock?days=90` | Dead stock detection |
| GET | `/api/analytics/reorder-suggestions?salesDays=30&leadTimeDays=7` | Reorder suggestions |
| GET | `/api/analytics/sales-trend?period=daily&days=30` | Sales trend (daily/weekly/monthly) |
| GET | `/api/analytics/purchase-vs-sales?days=30` | Purchase vs Sales comparison |
| GET | `/api/analytics/warehouse-stock` | Warehouse-wise stock summary |

### Inventory
| Method | Endpoint | Description |
|---|---|---|
| GET/POST | `/api/products` | List / Create products |
| GET/PUT/DELETE | `/api/products/{id}` | Get / Update / Delete product |
| GET | `/api/products/barcode/{code}` | Lookup product by barcode |
| GET/POST | `/api/inventory` | List / Create inventory records |
| GET/POST | `/api/stock-movements` | List / Create stock movements |
| GET/POST | `/api/purchases` | List / Create purchases |
| GET/POST | `/api/sales` | List / Create sales |
| GET/POST | `/api/invoices` | List / Create invoices |

### Management
| Method | Endpoint | Description |
|---|---|---|
| GET/POST | `/api/warehouses` | List / Create warehouses |
| GET/POST | `/api/categories` | List / Create categories |
| GET/POST | `/api/suppliers` | List / Create suppliers |
| GET/POST | `/api/customers` | List / Create customers |

## Frontend Pages

| URL | Page |
|---|---|
| `/login` | Login page |
| `/dashboard` | Dashboard overview with stats + charts |
| `/stock` | Stock levels with threshold filter |
| `/analytics/sales-trend` | Sales trend line chart |
| `/analytics/purchase-vs-sales` | Purchase vs Sales bar chart |
| `/analytics/velocity` | Stock velocity classification |
| `/analytics/abc` | ABC analysis with pie chart |
| `/analytics/dead-stock` | Dead stock items list |
| `/analytics/reorder` | Reorder suggestions |
| `/analytics/warehouse-stock` | Warehouse stock overview |

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/inventory_erp` | Database URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `Uday@2006` | Database password |
| `SHOW_SQL` | `false` | Show SQL queries in logs |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | - | SMTP username |
| `MAIL_PASSWORD` | - | SMTP password |
