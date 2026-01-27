# 🔐 SecureAuthX - Spring Boot Role-Based Authentication System (JWT)

A **production-grade Role-Based Authentication & Authorization system** built using **Spring Boot, Spring Security, JWT**, and **MongoDB/MySQL**.
This project follows **enterprise-level security standards** and demonstrates real-world backend architecture including secure authentication flows, role-based access, centralized exception handling, and stateless security design.

> Designed for **real-world applications**, **enterprise projects**, and **placement-ready backend systems**.

---

## 🚀 Core Capabilities

### 🔑 Authentication

* Secure user **registration & login**
* **BCrypt password hashing**
* Email uniqueness validation
* JWT token generation

### 🧩 Authorization

* **Role-Based Access Control (RBAC)** (`ROLE_USER`, `ROLE_ADMIN`)
* Method-level security using `@PreAuthorize`
* Endpoint-level access protection

### 🛡 Security Architecture

* Stateless authentication (`SessionCreationPolicy.STATELESS`)
* Custom JWT filter (`JwtAuthFilter`)
* RFC 7518 compliant **256-bit JWT signing key**
* Token validation & tamper detection

### ⚠️ Error Handling

* Custom authentication error handler (401)
* Custom authorization handler (403)
* Global exception handling (`@ControllerAdvice`)
* Standardized error response format

### 🧪 API Testing

* Fully testable using **Postman**
* End-to-end flow: Register → Login → Access APIs

---

## 🧱 Tech Stack

| Layer       | Technology      |
| ----------- | --------------- |
| Language    | Java 17+        |
| Framework   | Spring Boot     |
| Security    | Spring Security |
| Auth        | JWT (jjwt)      |
| ORM         | Spring Data JPA |
| Database    | MongoDB / MySQL |
| API Testing | Postman         |

---

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
│   ├── AdminController.java
│   └── UserController.java
│
├── service
│   ├── AuthService.java
│   ├── AdminService.java
│   └── UserService.java
│
├── entity
│   ├── BlacklistedToken.java
│   ├── User.java
│   └── ErrorResponse.java
│
├── repository
│   ├── BlacklistedTokenRepository.java
│   └── UserRepository.java
│
├── util
│   └── JwtUtil.java
│
├── filter
│   └── JwtAuthFilter.java
│
├── model
│   ├── SecurityConstants.java
│   └── Role.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── JwtAccessDeniedHandler.java
│   ├── JwtAuthEntryPoint.java
│   └── ValidationExceptionHandler.java
│
└── dto
    ├── RegisterRequestDto.java
    ├── LoginRequestDto.java
    ├── AdminDashboardResponseDto.java
    ├── UserProfileResponseDto.java
    ├── UserSummaryResponseDto.java
    └── AuthResponseDto.java
```

---

## 🔑 API Endpoints

### 📝 Register User

`POST /api/auth/register`

```json
{
  "name": "Sahil",
  "email": "sahil@gmail.com",
  "password": "password123"
}
```

---

### 🔐 Login User

`POST /api/auth/login`

```json
{
  "email": "sahil@gmail.com",
  "password": "password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 🔒 Access Secured Endpoint

Header:

```
Authorization: Bearer <JWT_TOKEN>
```

Example:
`GET /api/user/profile`

---

### 🛑 Admin-Only Endpoint

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public String adminOnly() {
    return "Admin Access Granted";
}
```

---

## ❌ Standard Error Response Format

```json
{
  "timestamp": "2026-01-25T18:21:08",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid JWT token",
  "path": "/api/user/profile"
}
```

---

## 🔒 Security Best Practices

* BCrypt password encryption with salting
* No plain-text password storage
* Stateless JWT authentication
* Secure HS256 token signing (256-bit key)
* Role-based API access control
* Account lockout after failed attempts
* JWT token blacklisting
* Centralized exception handling
* Jakarta Bean Validation
* Secure request filtering

---

## 🧪 Postman Testing Flow

1. Register → `/api/auth/register`
2. Login → `/api/auth/login`
3. Copy JWT Token
4. Add Authorization Header
5. Access protected APIs

---

## 📌 Roadmap / Future Enhancements

* Refresh token mechanism
* Token revocation strategy
* OAuth2 integration
* Social login (Google/GitHub)
* Multi-factor authentication (MFA)
* API rate limiting

---

## 👨‍💻 Author

**Sahil Gupta**
Backend Developer | Java | Spring Boot | Security Architecture

---

⭐ If you find this project useful, consider giving it a star and using it as a production template.
