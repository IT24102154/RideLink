# RideLink

A university microservices project — a university ride-sharing platform built with Spring Boot and MongoDB.

## Services

| Service | Port | Responsibility |
|---------|------|---------------|
| Ride-Management-Service | 8080 | Ride lifecycle management & integration |
| Driver-Vehicle-Service | 8081 | Driver & vehicle management |
| Account-Service | 8082 | User registration, login, JWT authentication |
| Fare-Payment-Service | 8083 | Fare estimation and simulated payment processing |

---

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.5
- **Database:** MongoDB (all services)
- **Auth:** JWT (JJWT 0.11.5)
- **API Docs:** SpringDoc OpenAPI / Swagger UI
- **Build:** Maven

---

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- MongoDB running on `localhost:27017`

---

## Quick Start

### 1. Clone and configure
```bash
git clone <repo-url>
cd RideLink
cp .env.example .env
# Edit .env with your MongoDB URI and JWT secret
```

### 2. Build & test all services
```bash
./mvnw clean test
```

### 3. Run each service
Open four terminals:
```bash
# Terminal 1
cd Ride-Management-Service && ./mvnw spring-boot:run

# Terminal 2
cd Driver-Vehicle-Service && ./mvnw spring-boot:run

# Terminal 3
cd Account-Service && ./mvnw spring-boot:run

# Terminal 4
cd Fare-Payment-Service && ./mvnw spring-boot:run
```

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27017/<service_db>` | MongoDB connection string |
| `SERVER_PORT_<SERVICE>` | Service-specific (8080/8081/8082/8083) | Override service port |
| `JWT_SECRET` | (configured in application.properties) | 256-bit JWT signing key |

---

## API Overview

### Account-Service — `http://localhost:8082`
| Method | Path | Body |
|--------|------|------|
| `POST` | `/api/v1/auth/register` | `{name, email, password, phoneNumber}` |
| `POST` | `/api/v1/auth/login` | `{email, password}` |

### Driver-Vehicle-Service — `http://localhost:8081`
| Method | Path | Notes |
|--------|------|-------|
| `GET` | `/drivers` | List all drivers |
| `POST` | `/drivers` | Create driver |
| `PUT` | `/drivers/{id}` | Update driver |
| `DELETE` | `/drivers/{id}` | Delete driver |
| `GET` | `/drivers/available?location=X` | Filter by location |
| `PUT` | `/drivers/{dId}/vehicle/{vId}` | Assign vehicle |
| `PUT` | `/drivers/{dId}/reserve` | Reserve driver for ride |
| `GET` | `/vehicles` | List all vehicles |
| `POST` | `/vehicles` | Create vehicle |

### Ride-Management-Service — `http://localhost:8080`
| Method | Path | Notes |
|--------|------|-------|
| `POST` | `/api/rides` | Create ride |
| `GET` | `/api/rides` | List all rides |
| `GET` | `/api/rides/{id}` | Get ride |
| `PUT` | `/api/rides/{id}/status` | Update status (calls payment on COMPLETED) |
| `PUT` | `/api/rides/{id}/assign` | Request auto-assignment from Driver-Vehicle |

### Fare-Payment-Service — `http://localhost:8083`
| Method | Path | Notes |
|--------|------|-------|
| `GET` | `/api/payments/estimate` | Estimate fare |
| `POST` | `/api/payments/process` | Record payment |
| `GET` | `/api/payments/{id}` | Get payment |

---

## Swagger UI
- Ride-Management-Service: http://localhost:8080/swagger-ui/index.html
- Driver-Vehicle-Service: http://localhost:8081/swagger-ui/index.html
- Account-Service: http://localhost:8082/swagger-ui/index.html
- Fare-Payment-Service: http://localhost:8083/swagger-ui/index.html

---

## Project Structure

```
RideLink/
├── pom.xml                        ← Root aggregator POM
├── .env.example                   ← Environment template
├── Account-Service/               ← JWT auth service
├── Driver-Vehicle-Service/        ← Driver & fleet management
├── Fare-Payment-Service/          ← Fare & payments
└── Ride-Management-Service/       ← Ride lifecycle service
```

---

## Test Results

```
account-service         — 3 ✅ passed
driver-vehicle-service  — 10 ✅ passed
ride-management-service — 7 ✅ passed
fare-payment-service    — 4 ✅ passed
```
