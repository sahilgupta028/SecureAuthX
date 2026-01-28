package com.example.roleAuthentication.controller;

import com.example.roleAuthentication.dto.AuthResponseDto;
import com.example.roleAuthentication.dto.LoginRequestDto;
import com.example.roleAuthentication.dto.RegisterRequestDto;
import com.example.roleAuthentication.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request, HttpServletRequest httpRequest) {
        authService.register(request, httpRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
        return authService.login(request,  httpRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }
}