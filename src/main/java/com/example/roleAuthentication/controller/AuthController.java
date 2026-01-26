package com.example.roleAuthentication.controller;

import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.dto.AuthResponseDto;
import com.example.roleAuthentication.dto.LoginRequestDto;
import com.example.roleAuthentication.dto.RegisterRequestDto;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.model.Role;
import com.example.roleAuthentication.repository.UserRepository;
import com.example.roleAuthentication.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto req) {

        if (userRepository.findByEmail(req.email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setRole(req.role == null ? Role.ROLE_USER : req.role);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto req) {

        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new GlobalExceptionHandler.UnauthorizedException("Invalid email or password");
        }

        AuthResponseDto response = new AuthResponseDto();
        response.accessToken = jwtUtil.generateToken(user);
        response.refreshToken = UUID.randomUUID().toString();

        return response;
    }
}
