# 🔐 SecureAuthX – Spring Boot Role-Based Authentication System (JWT)

SecureAuthX is a **production-grade Role-Based Authentication & Authorization system** built using **Spring Boot, Spring Security, JWT**, and **MongoDB**.
The project is designed following **enterprise-level backend architecture**, focusing on security, scalability, clean layering, and real-world use cases.

> 🚀 Suitable for **real-world applications**, **enterprise backend services**, and **placement / interview-ready projects**.

---

## ✨ Key Highlights

* Stateless authentication using JWT
* Role-based access control (USER / ADMIN)
* Account lockout on multiple failed login attempts
* JWT token blacklisting on logout
* Centralized exception handling with clean API responses
* Proper Controller–Service–Repository separation

---

## 🚀 Core Capabilities

### 🔑 Authentication

* Secure user **registration & login**
* **BCrypt password hashing** (no plain-text storage)
* Email uniqueness validation
* JWT access token generation

### 🧩 Authorization

* **Role-Based Access Control (RBAC)** (`ROLE_USER`, `ROLE_ADMIN`)
* Method-level security using `@PreAuthorize`
* Endpoint-level access protection via Spring Security

### 🛡 Security Architecture

* Stateless authentication (`SessionCreationPolicy.STATELESS`)
* Custom JWT authentication filter (`JwtAuthFilter`)
* RFC 7518 compliant **256-bit JWT signing key (HS256)**
* Token tamper detection and validation

### 🔐 Advanced Security Features

* Account lockout after configurable failed login attempts
* Automatic account unlock after lock duration
* JWT token blacklisting on logout
* Secure request filtering before controller execution

### ⚠️ Error Handling

* Custom authentication error response (401)
* Custom authorization error response (403)
* Global exception handling using `@ControllerAdvice`
* Consistent and sanitized error response format

### 🧪 API Testing

* Fully testable using **Postman**
* End-to-end flow: Register → Login → Access Secured APIs

---

## 🧱 Tech Stack

| Layer          | Technology                |
| -------------- | ------------------------- |
| Language       | Java 17+                  |
| Framework      | Spring Boot               |
| Security       | Spring Security           |
| Authentication | JWT (jjwt)                |
| Data Access    | Spring Data (MongoDB)     |
| Build Tool     | Maven                     |
| Database       | MongoDB                   |
| API Testing    | Postman                   |

---

## 🔄 How Security Works (High‑Level Flow)

```text
┌──────────────────┐
│      Client      │
│ (Postman / UI)   │
└─────────┬────────┘
          │ 1️⃣ Login (email + password)
          ▼
┌──────────────────┐
│   Auth API       │
│  /api/auth/login │
└─────────┬────────┘
          │ 2️⃣ Credentials verified (BCrypt)
          ▼
┌──────────────────┐
│   JWT Issued     │
│  HS256 (256-bit) │
└─────────┬────────┘
          │
          │  Authorization: Bearer <JWT>
          ▼
┌───────────────────────────────────────────────────────────┐
│           Spring Security Filter Chain                    │
│                                                           │
│   ┌───────────────────────────────────────────────────┐   │
│   │               JwtAuthFilter                       │   │
│   │                                                   │   │
│   │  • Extract JWT from Authorization header          │   │
│   │  • Validate signature & expiration                │   │
│   │  • Check token blacklist (logout protection)      │   │
│   │  • Load user details & roles                      │   │
│   │  • Set Authentication in SecurityContext          │   │
│   └───────────────────────────────────────────────────┘   │
│                                                           │
└─────────┬─────────────────────────────────────────────────┘
          │
          ▼
┌──────────────────┐
│  Controller      │
│ (@PreAuthorize)  │
└─────────┬────────┘
          │
          ▼
┌──────────────────┐
│   Service Layer  │
│ (Business Logic) │
└─────────┬────────┘
          │
          ▼
┌──────────────────┐
│    Database      │
│ (Users / Tokens) │
└──────────────────┘
```

## 📂 Project Architecture

```
com.example.roleAuthentication
│
├── config
│   ├── SecurityConfig.java
│   └── PasswordConfig.java
│
├── constants
│   ├── RateLimitConstants.java
│   └── SecurityConstants.java
│
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   └── AdminController.java
│
├── service
│   ├── AuthService.java
│   ├── UserService.java
│   ├── RateLimitService.java
│   ├── AuditLogService.java
│   └── AdminService.java
│
├── entity
│   ├── User.java
│   ├── AuditLog.java
│   ├── BlacklistedToken.java
│   └── ErrorResponse.java
│
├── repository
│   ├── UserRepository.java
│   ├── AuditLogRepository.java
│   └── BlacklistedTokenRepository.java
│
├── dto
│   ├── RegisterRequestDto.java
│   ├── LoginRequestDto.java
│   ├── AuthResponseDto.java
│   ├── AuditLogResponseDto.java
│   ├── UserProfileResponseDto.java
│   ├── UserSummaryResponseDto.java
│   └── AdminDashboardResponseDto.java
│
├── filter
│   ├── RateLimitFilter.java
│   └── JwtAuthFilter.java
│
├── util
│   └── JwtUtil.java
│
├── model
│   ├── Role.java
│   ├── AuditAction.java
│   └── RateLimit.java
│
└── exception
    ├── GlobalExceptionHandler.java
    ├── JwtAuthEntryPoint.java
    ├── JwtAccessDeniedHandler.java
    └── ValidationExceptionHandler.java
```

---

## 🔑 API Endpoints Overview

### 📝 Authentication APIs

| Method | Endpoint             | Description              |
| ------ | -------------------- | ------------------------ |
| POST   | `/api/auth/register` | Register new user        |
| POST   | `/api/auth/login`    | Login & get JWT          |
| POST   | `/api/auth/logout`   | Logout & blacklist token |

#### Register User

```json
{
  "name": "Sahil",
  "email": "sahil@gmail.com",
  "password": "password123",
  "role": "ROLE_USER"
}
```

#### Login Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "uuid-string"
}
```

---

### 👤 User APIs (USER / ADMIN)

| Method | Endpoint            | Description                |
| ------ | ------------------- | -------------------------- |
| GET    | `/api/user/profile` | Get logged-in user profile |

---

### 🛑 Admin APIs (ADMIN Only)

| Method | Endpoint                       | Description             |
| ------ | ------------------------------ | ----------------------- |
| GET    | `/api/admin/dashboard`         | Admin dashboard metrics |
| GET    | `/api/admin/users`             | List all users          |
| PUT    | `/api/admin/users/{id}/lock`   | Lock user account       |
| PUT    | `/api/admin/users/{id}/unlock` | Unlock user account     |
| GET    | `/api/admin/audit-logs`        | List all Audits         |
| GET    | `/api/admin/audit-logs/user/{email}` | Get Audit of particular user        |

---

## ❌ Standard Error Response Format

```json
{
  "timestamp": "2026-01-27T10:56:55",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid JWT token",
  "path": "/api/user/profile"
}
```
---

## 🔒 Security Defense-in-Depth (Rate Limiting + Account Lockout)

SecureAuthX uses a **layered security approach** to protect authentication endpoints and user accounts. Two independent but complementary mechanisms are implemented:

---

### 🧱 1. API Rate Limiting (Endpoint-Level Protection)

**Where it works:**

* Spring Security Filter Layer (`RateLimitFilter`)

**How it works:**

* Limits the number of requests per IP address
* Applied **before** authentication logic
* Prevents excessive requests from bots or scripts

**Configured Rule:**

* Max **5 requests per minute per IP** on `/api/auth/login`

**If limit exceeded:**

```http
HTTP 429 – Too Many Requests
```

**What it protects against:**

* Brute-force attacks
* Credential stuffing
* Automated bot traffic

---

### 🔐 2. Account Lockout (User-Level Protection)

**Where it works:**

* Service / Business Logic Layer (`AuthService`)

**How it works:**

* Tracks failed login attempts **per user account**
* Independent of request speed

**Configured Rule:**

* Account is locked after **5 consecutive incorrect passwords**
* Lock is time-bound and auto-unlocked after a cooldown period

**If account is locked:**

```http
HTTP 401 – Unauthorized
Account locked due to multiple failed login attempts
```

**What it protects against:**

* Targeted attacks on a specific user
* Manual password guessing

---

### 🔄 Combined Security Flow (Defense-in-Depth)

```
Client Request
      │
      ▼
RateLimitFilter (IP-based)
      │
      ├─ Too many requests? → 429 (Blocked)
      │
      ▼
AuthController
      ▼
AuthService
      │
      ├─ Wrong password? → increment failedAttempts
      ├─ failedAttempts ≥ 5 → lock account
      ▼
Authentication Result
```

---

### 🧠 Why Both Are Needed

| Scenario           | Rate Limiter     | Account Lock      |
| ------------------ | ----------------- | ---------------- |
| Bot attack (fast)  | ✅ Blocks        | ❌ Not triggered |
| Slow manual attack | ❌ Not triggered | ✅ Locks account |
| Aggressive attack  | ✅ Blocks        | ✅ Locks account |

This **defense-in-depth strategy** ensures that:

* APIs are protected at the network level
* User accounts are protected at the business logic level
* No single security control is relied upon

> This is a **production-grade security pattern** commonly used in enterprise authentication systems.

---

## 📝 Audit Logging (Security & Compliance)

SecureAuthX implements **centralized audit logging** to track all critical security and user actions. Audit logs are stored in **MongoDB**, ensuring immutability, scalability, and fast querying for security reviews and compliance.

### 🎯 Why Audit Logs Matter

Audit logging helps in:

* Detecting suspicious or malicious activity
* Investigating security incidents
* Meeting compliance and enterprise security requirements
* Maintaining a traceable history of sensitive operations

### 🔍 What Actions Are Logged

The system records audit events for the following actions:

| Action              | Description                                           |
| ------------------- | ----------------------------------------------------- |
| `REGISTER_SUCCESS`  | Successful user registration                          |
| `REGISTER_FAILED`   | Registration failed (email exists, validation errors) |
| `LOGIN_SUCCESS`     | Successful authentication                             |
| `LOGIN_FAILED`      | Invalid credentials attempt                           |
| `ACCOUNT_LOCKED`    | Account locked after max failed attempts              |
| `LOGOUT`            | User logout (JWT blacklisted)                         |
| `ADMIN_LOCK_USER`   | Admin manually locks a user                           |
| `ADMIN_UNLOCK_USER` | Admin unlocks a user account                          |

### 🗂 Audit Log Data Model (MongoDB)

```json
{
  "id": "65f12e9a4c9eab1234567890",
  "username": "sahil@gmail.com",
  "action": "LOGIN_SUCCESS",
  "ipAddress": "192.168.1.10",
  "userAgent": "PostmanRuntime/7.36.0",
  "endpoint": "/api/auth/login",
  "timestamp": "2026-01-27T21:15:32"
}
```

### 🏗 Architecture Placement

```text
Controller  ──▶  Service Layer  ──▶  AuditLogService  ──▶  MongoDB
                        │
                        └── Business Logic (Auth / Admin)
```

### 🔐 Security & Privacy Considerations

* No passwords or JWT tokens are stored in audit logs
* IP address and User-Agent are captured for traceability
* Logs are append-only (no update/delete operations)
* Suitable for SIEM and monitoring integrations

### 🧪 Viewing Audit Logs

```http
GET /api/admin/audit-logs
Authorization: Bearer <ADMIN_JWT>
```

---

## 🔒 Security Best Practices Implemented

* BCrypt password hashing with salting
* No plain-text password storage or logging
* Stateless JWT authentication
* Secure HS256 token signing (256-bit secret)
* Role-based API authorization
* Account lockout on multiple failed login attempts
* JWT token blacklisting on logout
* Centralized exception handling
* Jakarta Bean Validation for input validation
* Secure JWT request filtering
* API rate limiting
* Audit Logging

---

## 🧪 Postman Testing Flow

1. Register user → `/api/auth/register`
2. Login → `/api/auth/login`
3. Copy JWT access token
4. Add `Authorization: Bearer <TOKEN>` header
5. Access secured USER / ADMIN APIs

---

## 📌 Roadmap / Future Enhancements

* Refresh token persistence
* Token revocation strategy
* OAuth2 & social login
* Multi-Factor Authentication (MFA)

---

## 👨‍💻 Author

**Sahil Gupta**
Backend Developer | Java | Spring Boot | Security Architecture

---

⭐ If you find this project useful, consider giving it a star and using it as a production-ready authentication template.
