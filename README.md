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
┌──────────┐       ┌──────────────┐       ┌──────────────────┐
│  Client  │──────▶│ Auth API     │──────▶│ JWT Issued       │
│ (Postman │       │ (/login)     │       │ (HS256 Signed)  │
│  / UI)   │       └──────────────┘       └──────────────────┘
│    │                                           │
│    │  Authorization: Bearer <JWT>              │
│    ▼                                           ▼
│ ┌─────────────────────────────────────────────────────────┐
│ │              Spring Security Filter Chain                │
│ │                                                         │
│ │  JwtAuthFilter                                          │
│ │   ├─ Extract JWT from header                             │
│ │   ├─ Validate signature & expiry                         │
│ │   ├─ Check blacklist (logout tokens)                     │
│ │   ├─ Load user & roles                                   │
│ │   └─ Inject Authentication into SecurityContext          │
│ │                                                         │
│ └─────────────────────────────────────────────────────────┘
│                        │
│                        ▼
│               Controller Layer
│        (@PreAuthorize / Role Checks)
│                        │
│                        ▼
│                  Service Layer
│                        │
│                        ▼
│                   Database
└─────────────────────────────────────────────────────────────┘
```

## 📂 Project Architecture

```
com.example.roleAuthentication
│
├── config
│   ├── SecurityConfig.java
│   └── PasswordConfig.java
│
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   └── AdminController.java
│
├── service
│   ├── AuthService.java
│   ├── UserService.java
│   └── AdminService.java
│
├── entity
│   ├── User.java
│   ├── BlacklistedToken.java
│   └── ErrorResponse.java
│
├── repository
│   ├── UserRepository.java
│   └── BlacklistedTokenRepository.java
│
├── dto
│   ├── RegisterRequestDto.java
│   ├── LoginRequestDto.java
│   ├── AuthResponseDto.java
│   ├── UserProfileResponseDto.java
│   ├── UserSummaryResponseDto.java
│   └── AdminDashboardResponseDto.java
│
├── filter
│   └── JwtAuthFilter.java
│
├── util
│   └── JwtUtil.java
│
├── model
│   ├── Role.java
│   └── SecurityConstants.java
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
* API rate limiting
* Audit logging

---

## 👨‍💻 Author

**Sahil Gupta**
Backend Developer | Java | Spring Boot | Security Architecture

---

⭐ If you find this project useful, consider giving it a star and using it as a production-ready authentication template.
