# ⚙️ RailLanka Pro — Backend

The backend is a **RESTful API** built with **Spring Boot 3.5.4** and **Java 21**. It is the single source of truth for both the Staff Dashboard and the Passenger Portal — handling authentication, business logic, PDF/QR generation, email/SMS delivery, and payment integration.

> 🔗 **Root Project:** [Back to Main README](../README.md)

---

## 📑 Table of Contents

1. [Tech Stack](#-tech-stack)
2. [Prerequisites](#-prerequisites)
3. [Installation & Setup](#-installation--setup)
4. [Environment Variables](#-environment-variables--applicationproperties)
5. [Folder Structure](#-folder-structure)
6. [Architectural Pattern](#-architectural-pattern)
7. [Entity & Database Schema](#-entity--database-schema-overview)
8. [API Endpoints](#-api-endpoints-summary)
9. [Build & Run Commands](#-build--run-commands)
10. [Email Templates](#-email-templates)

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.5.4 |
| **Security** | Spring Security + JJWT | 0.11.5 |
| **ORM** | Spring Data JPA / Hibernate | (Boot-managed) |
| **Database** | MySQL | 8+ |
| **DB Driver** | MySQL Connector/J | (Boot-managed) |
| **Mapping** | ModelMapper | 3.2.4 |
| **Boilerplate** | Lombok | (Boot-managed) |
| **PDF Generation** | OpenPDF | 1.3.30 |
| **QR Code** | ZXing Core + JavaSE | 3.5.3 |
| **Email** | Spring Boot Mail (SMTP) | (Boot-managed) |
| **Templates** | Thymeleaf + Security extras | (Boot-managed) |
| **Monitoring** | Spring Boot Actuator | (Boot-managed) |
| **Payment** | PayHere Gateway | REST |
| **SMS** | Notify.lk API | REST |
| **Build Tool** | Apache Maven | 3.8+ |

---

## ✅ Prerequisites

Before running the backend, ensure you have the following installed:

- ☕ **Java Development Kit (JDK) 21** — [Download](https://adoptium.net/)
- 📦 **Apache Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) (or use the included `mvnw` wrapper)
- 🗄️ **MySQL Server 8.0+** — [Download](https://dev.mysql.com/downloads/mysql/)
- 🧰 **An IDE** — IntelliJ IDEA (recommended), VS Code, or Eclipse

Verify your installations:
```bash
java -version
mvn -version
mysql --version
```

---

## 🚀 Installation & Setup

### Step 1 — Clone the Repository
```bash
git clone https://github.com/ManuthLakdiw/RailLanka-Pro.git
cd "RailLanka-Pro/Back-End"
```

### Step 2 — Configure MySQL
Start your MySQL server and create a user (the app will auto-create the database):
```sql
-- Optional: create a dedicated user
CREATE USER 'railuser'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON raillankapro.* TO 'railuser'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3 — Configure `application.properties`
Edit `src/main/resources/application.properties` with your local credentials (see [Environment Variables](#-environment-variables--applicationproperties) section below).

### Step 4 — Install Dependencies
```bash
./mvnw dependency:resolve
# or on Windows:
mvnw.cmd dependency:resolve
```

### Step 5 — Run the Application
```bash
./mvnw spring-boot:run
```

The API will start at:
```
http://localhost:8080
```

---

## 🔧 Environment Variables / `application.properties`

All configuration lives in `src/main/resources/application.properties`. Below is a full template with explanations:

```properties
# ─── Application ───────────────────────────────────────────────
spring.application.name=RailLanka Pro - Backend

# ─── JWT Configuration ─────────────────────────────────────────
# Access token lifetime in milliseconds (300000 = 5 minutes)
access.token.expiration.time=300000

# Secret key used to sign JWT tokens (keep this private!)
jwt.secret.key=YOUR_VERY_LONG_SECRET_KEY_HERE

# Refresh token lifetime in seconds (1800 = 30 minutes)
refresh.token.expiration.time=1800

# ─── Database ──────────────────────────────────────────────────
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/raillankapro?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
# HikariCP connection pool size
sprig.datasource.hikari.maximum-pool-size=20

# ─── JPA / Hibernate ───────────────────────────────────────────
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
# 'update' auto-updates schema; use 'validate' in production
spring.jpa.hibernate.ddl-auto=update

# ─── Email (Gmail SMTP) ────────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your.email@gmail.com
# Use a Gmail App Password, NOT your real password
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ─── PayHere Payment Gateway ───────────────────────────────────
payhere.merchant.id=YOUR_MERCHANT_ID
payhere.merchant.secret=YOUR_MERCHANT_SECRET
payhere.currency=LKR

# ─── Notify.lk SMS Gateway ─────────────────────────────────────
notifylk.user_id=YOUR_USER_ID
notifylk.api_key=YOUR_API_KEY
notifylk.sender_id=NotifyDEMO
```

> ⚠️ **Security Warning:** Never commit real passwords or secrets to Git. Use environment variables or a secrets manager in production.

---

## 📁 Folder Structure

```
Back-End/
├── src/
│   ├── main/
│   │   ├── java/lk/ijse/raillankaprobackend/
│   │   │   ├── RailLankaProBackendApplication.java  ← Entry point
│   │   │   │
│   │   │   ├── config/                 ← Spring configuration beans
│   │   │   │   ├── ApplicationConfig.java   (BCrypt, ModelMapper, UserDetailsService)
│   │   │   │   └── SecurityConfig.java      (SecurityFilterChain, CORS, JWT filter)
│   │   │   │
│   │   │   ├── controller/             ← REST API layer (HTTP request handlers)
│   │   │   │   ├── AuthController.java          (public /auth/** endpoints)
│   │   │   │   ├── TrainController.java
│   │   │   │   ├── ScheduleController.java
│   │   │   │   ├── StationController.java
│   │   │   │   ├── EmployeeController.java
│   │   │   │   ├── PassengerController.java
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── CounterController.java
│   │   │   │   ├── StationMasterController.java
│   │   │   │   ├── PDFController.java           (report downloads)
│   │   │   │   ├── PaymentController.java
│   │   │   │   ├── CustomerSupportController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── DistanceController.java
│   │   │   │   └── HelloController.java         (health check)
│   │   │   │
│   │   │   ├── service/                ← Business logic interfaces
│   │   │   │   └── impl/              ← Concrete service implementations
│   │   │   │
│   │   │   ├── repository/             ← Spring Data JPA repositories
│   │   │   │
│   │   │   ├── entity/                 ← JPA entity classes (DB tables)
│   │   │   │   └── Dtypes/            ← Enums (TrainCategory, TravelClass, etc.)
│   │   │   │   └── projection/        ← JPA projections (read-only interfaces)
│   │   │   │
│   │   │   ├── dto/                    ← Data Transfer Objects (request/response)
│   │   │   │
│   │   │   ├── exception/              ← Custom exception classes
│   │   │   │
│   │   │   └── util/                   ← Utilities
│   │   │       ├── JwtUtil.java             (token generation & validation)
│   │   │       ├── JwtAuthFilter.java       (OncePerRequestFilter)
│   │   │       ├── ApiResponse.java         (standard response wrapper)
│   │   │       ├── PaginatedResponse.java   (paginated response wrapper)
│   │   │       └── StationSequenceUtil.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties   ← All app configuration
│   │       ├── images/                  ← Logo assets for PDF generation
│   │       └── templates/              ← Thymeleaf HTML email templates
│   │
│   └── test/                           ← JUnit test stubs
├── pom.xml                             ← Maven dependency manifest
├── mvnw / mvnw.cmd                     ← Maven wrapper scripts
└── HELP.md
```

---

## 🏛️ Architectural Pattern

The backend follows a strict **Layered (N-Tier) Architecture**:

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller │  ← Receives HTTP, delegates to service, returns ResponseEntity
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← Interface defining business operations
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Service Impl│  ← Concrete implementation (uses Repositories + ModelMapper)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ← Spring Data JPA (extends JpaRepository)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   MySQL DB  │
└─────────────┘
```

**Key Design Decisions:**
- **DTO ↔ Entity Mapping** is handled by **ModelMapper** in service implementations — controllers never touch entities directly.
- **`ApiResponse<T>`** is a generic wrapper used for all standard API responses (`code`, `message`, `data`).
- **`PaginatedResponse<T>`** extends `ApiResponse` with pagination metadata (`currentPage`, `totalPages`, `totalItems`, `startNumber`, `endNumber`).
- **Services are defined as interfaces** (e.g., `TrainService`) with implementations in the `impl/` sub-package (e.g., `TrainServiceImpl`), following the *programming to an interface* principle.

---

## 🗄️ Entity & Database Schema Overview

The schema is auto-generated by Hibernate (`ddl-auto=update`). Core entities:

| Entity | Table | Key Fields |
|--------|-------|-----------|
| `User` | `user` | `userId`, `username`, `password`, `role` (enum) |
| `Passenger` | `passenger` | `passengerId`, `name`, `nic`, `idType`, `active` |
| `Admin` | `admin` | `adminId`, linked to `User` |
| `StationMaster` | `station_master` | `stationMasterId`, `station` FK |
| `Counter` | `counter` | `counterId`, `counterNumber` (enum) |
| `Employee` | `employee` | `employeeId`, `position` (enum), `station` FK |
| `Train` | `train` | `trainId`, `name`, `category` (enum), `trainType` (enum) |
| `Station` | `station` | `stationId`, `name`, `code`, `active` |
| `Schedule` | `schedule` | `scheduleId`, `departureTime`, `arrivalTime`, `frequency` (enum) |
| `ScheduleIntermediateStop` | `schedule_intermediate_stop` | stop time, station FK |
| `Booking` | `booking` | `bookingId`, `travelDate`, `travelClass` (enum) |
| `Ticket` | `ticket` | `ticketId`, `status` (enum), booking FK |
| `FirstClassBookingSeat` | `first_class_booking_seat` | `row` (enum), `seat` (enum) |
| `TicketBookingPayment` | `ticket_booking_payment` | payment reference |
| `RefreshToken` | `refresh_token` | `token`, `expiryDate`, user FK |
| `GoodsTrain` | `goods_train` | `cargoType` (enum), train FK |
| `SpecialTrain` | `special_train` | `specialType` (enum), train FK |

**Enum Types defined in `entity/Dtypes/`:**

| Enum | Values |
|------|--------|
| `SystemUserRole` | `ADMIN`, `STATION_MASTER`, `COUNTER`, `PASSENGER` |
| `TrainCategory` | `POST`, `PASSENGER`, `GOODS`, `SPECIAL` |
| `TrainType` | `EXPRESS`, `INTERCITY`, `INTERMEDIATE` |
| `TravelClass` | `FIRST`, `SECOND`, `THIRD` |
| `ScheduleFrequency` | Daily, Weekday, Weekend, etc. |
| `EmployeePosition` | Various staff roles |
| `PassengerType` | `LOCAL`, `FOREIGN` |
| `TicketStatus` | `ACTIVE`, `USED`, `CANCELLED` |

---

## 📡 API Endpoints Summary

**Base URL:** `http://localhost:8080/api/v1/raillankapro`

### 🔓 Public Endpoints (No Auth Required) — `/auth/**`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/login` | Authenticate user, get access + refresh token |
| `POST` | `/auth/register/passenger` | Register a new passenger |
| `POST` | `/auth/refreshtoken` | Get new access token using refresh token |
| `POST` | `/auth/reset/password/verify?email=` | Send OTP for password reset |
| `POST` | `/auth/reset/password/verify/code?email=&code=` | Verify OTP code |
| `POST` | `/auth/reset/password` | Reset password |
| `GET` | `/auth/stations` | Get all station names & codes |
| `POST` | `/auth/schedules/{pageNo}/{pageSize}` | Search train schedules (paginated) |
| `POST` | `/auth/calc/clases/ticket/price?scheduleid=` | Calculate ticket price by class |
| `GET` | `/auth/booking/detail/by?bookingid=` | Get booking details |
| `GET` | `/auth/download/ticket?bookingid=` | Download PDF ticket |
| `GET` | `/auth/qr/{bookingId}` | Get QR code image for booking |
| `POST` | `/auth/sms/{bookingId}/{phoneNumber}` | Send ticket SMS |

### 🔒 Protected Endpoints (JWT Required)

#### 🚆 Trains — `/train`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/train/register` | Add new train |
| `GET` | `/train/getall/{pageNo}/{pageSize}` | Get all trains (paginated) |
| `PUT` | `/train/update` | Update train details |
| `PUT` | `/train/changestatus/{trainId}/{status}` | Activate / deactivate train |
| `PUT` | `/train/delete?id=` | Soft-delete train |
| `GET` | `/train/filter/{pageNo}/{pageSize}?keyword=` | Filter by keyword |
| `GET` | `/train/count` | Get total/active/inactive counts |
| `GET` | `/train/count/by/type` | Get count by train type |

#### 📅 Schedules — `/schedule`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/schedule/register` | Create new schedule |
| `GET` | `/schedule/getall/{pageNo}/{pageSize}` | Get all schedules (paginated) |
| `PUT` | `/schedule/update` | Update schedule |
| `PUT` | `/schedule/change/status/{id}/{status}` | Toggle schedule status |
| `PUT` | `/schedule/delete?id=` | Soft-delete schedule |
| `GET` | `/schedule/count` | Get schedule counts |
| `GET` | `/schedule/avg/daily` | Average daily trips |

#### 🏢 Stations — `/station`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/station/register` | Add new station |
| `GET` | `/station/getall/{pageNo}/{pageSize}` | Get all stations (paginated) |
| `PUT` | `/station/update` | Update station |
| `PUT` | `/station/changestatus/{id}/{status}` | Toggle station status |

#### 👩‍💼 Employees — `/employee`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/employee/register` | Add employee |
| `GET` | `/employee/getall/{pageNo}/{pageSize}` | All employees (paginated) |
| `PUT` | `/employee/update` | Update employee |
| `PUT` | `/employee/delete?id=` | Remove employee |

#### 📄 PDF Reports — `/pdf`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/pdf/download/all/trains` | All trains PDF |
| `GET` | `/pdf/download/active/trains` | Active trains PDF |
| `GET` | `/pdf/download/all/schedules` | All schedules PDF |
| `GET` | `/pdf/download/all/passengers` | All passengers PDF |
| `GET` | `/pdf/download/all/employees` | All employees PDF |
| `GET` | `/pdf/download/by/station?station=` | Employees by station PDF |
| `GET` | `/pdf/download/all/stationmasters` | Station masters PDF |
| `GET` | `/pdf/download/all/counters` | Counters PDF |

---

## 🔐 Authentication Flow

```
1. POST /auth/login  →  { accessToken, refreshToken, role }
2. Store tokens in browser (localStorage / sessionStorage)
3. Attach header to all protected requests:
       Authorization: Bearer <accessToken>
4. On 401 Expired response → POST /auth/refreshtoken  →  new accessToken
5. On logout / token invalid → clear storage, redirect to login
```

The `JwtAuthFilter` (extends `OncePerRequestFilter`) intercepts every request, extracts the Bearer token, validates it via `JwtUtil`, and populates the `SecurityContextHolder`.

---

## 📧 Email Templates

Thymeleaf HTML templates in `src/main/resources/templates/`:

| Template File | Purpose |
|---------------|---------|
| `verification-code-email.html` | OTP for password reset |
| `counter-credential-email.html` | Welcome email for new counters |
| `station-master-credential-email.html` | Welcome email for station masters |
| `client-support-confirmation-email.html` | Customer support ticket confirmation |
| `it-support-email.html` | Internal IT support notification |

---

## 🏗️ Build & Run Commands

```bash
# Run in development mode
./mvnw spring-boot:run

# Run tests
./mvnw test

# Package as executable JAR
./mvnw clean package

# Run the packaged JAR
java -jar target/RailLankaPro-Backend-0.0.1-SNAPSHOT.jar

# Skip tests during build
./mvnw clean package -DskipTests

# Check dependency tree
./mvnw dependency:tree
```

> 💡 On Windows, replace `./mvnw` with `mvnw.cmd`

---

## 🩺 Health Check

Spring Boot Actuator is enabled. After startup:
```
GET http://localhost:8080/actuator/health
```
Expected response:
```json
{ "status": "UP" }
```

---

> 🔗 **Frontend Documentation:** [Fornt-End README](../Fornt-End/README.md)
