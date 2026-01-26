# 🔐 Spring Boot Role-Based Authentication (JWT)

This project demonstrates a **complete Role-Based Authentication & Authorization system** built with **Spring Boot, Spring Security, JWT**, and **MySQL**. It includes **custom error responses**, **secure password handling**, and is fully **testable using Postman**.

---

## 🚀 Features Implemented

### ✅ Authentication

* User **Registration**
* User **Login** with JWT
* Password encryption using **BCrypt**
* Email uniqueness validation

### ✅ Authorization

* **Role-based access control** (e.g. `ROLE_USER`, `ROLE_ADMIN`)
* Method-level security using `@PreAuthorize`
* Secure endpoint protection using JWT

### ✅ Security

* Stateless authentication (`STATELESS` session policy)
* Custom JWT filter (`JwtAuthFilter`)
* Secure **256-bit JWT signing key** (RFC 7518 compliant)

### ✅ Error Handling (Custom Responses)

* Custom authentication error response (401)
* Custom access denied response (403)
* Global exception handling using `@ControllerAdvice`
* Consistent error JSON format

### ✅ Testing

* Fully testable via **Postman**
* Step-by-step API flow (Register → Login → Access Protected APIs)

---

## 🧱 Tech Stack

* Java 17+
* Spring Boot
* Spring Security
* JWT (jjwt)
* Spring Data JPA
* MySQL
* Postman

---

## 📂 Project Structure

```
com.example.security
│
├── config
│   ├── SecurityConfig.java
│   ├── PasswordConfig.java
│
├── controller
│   ├── AuthController.java
│   ├── AdminController.java
│   └── UserController.java
│
├── entity
│   ├── User.java
│   └── ErrorResponse.java
│
├── repository
│   ├── UserRepository.java
│
├── util
│   └── JwtUtil.java
│
├── filter
│   └── JwtAuthFilter.java
│
├── model
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
    └── AuthResponseDto.java
```

---

## 🔑 API Endpoints

### 🔹 Register User

`POST /api/auth/register`

```json
{
  "name": "Sahil",
  "email": "sahil@gmail.com",
  "password": "password123"
}
```

---

### 🔹 Login User

`POST /api/auth/login`

```json
{
  "email": "sahil@gmail.com",
  "password": "password123"
}
```

📌 Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 🔹 Access Protected Endpoint

Add Header in Postman:

```
Authorization: Bearer <JWT_TOKEN>
```

Example:
`GET /api/user/profile`

---

### 🔹 Admin Only Endpoint

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public String adminOnly() {
    return "Admin Access Granted";
}
```

---

## ❌ Custom Error Response Format

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

## 🧪 How to Test in Postman (Flow)

1. **Register User** → `/api/auth/register`
2. **Login** → `/api/auth/login`
3. Copy JWT token
4. Add token in Authorization Header
5. Access secured APIs

---

## 🔒 Security Best Practices Followed

* Encrypted passwords (BCrypt)
* JWT signed with 256-bit secret key
* No session storage
* Role-based access checks
* Centralized exception handling

---

## 📌 Future Enhancements

* Refresh Token flow
* Token revocation
* Account lock on failed attempts
* OAuth2 / Social Login

---

## 👨‍💻 Author

**Sahil Gupta**

Feel free to extend this project or integrate it into real-world applications 🚀
