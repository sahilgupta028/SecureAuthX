package com.example.roleAuthentication.controller;

import com.example.roleAuthentication.entity.BlacklistedToken;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.dto.AuthResponseDto;
import com.example.roleAuthentication.dto.LoginRequestDto;
import com.example.roleAuthentication.dto.RegisterRequestDto;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.model.Role;
import com.example.roleAuthentication.repository.BlacklistedTokenRepository;
import com.example.roleAuthentication.repository.UserRepository;
import com.example.roleAuthentication.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.example.roleAuthentication.model.SecurityConstants.LOCK_TIME_MINUTES;
import static com.example.roleAuthentication.model.SecurityConstants.MAX_FAILED_ATTEMPTS;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

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
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        // 🔒 Check if account is locked
        if (user.isAccountLocked()) {

            // ⏱ Auto-unlock if lock time expired
            if (user.getLockTime() != null &&
                    user.getLockTime().plusMinutes(LOCK_TIME_MINUTES).isBefore(LocalDateTime.now())) {

                user.setAccountLocked(false);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);

            } else {
                throw new GlobalExceptionHandler.UnauthorizedException(
                        "Account locked due to multiple failed attempts. Try again later."
                );
            }
        }

        // ❌ Password mismatch
        if (!passwordEncoder.matches(req.password, user.getPassword())) {

            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            // 🔐 Lock account after max attempts
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
            }

            userRepository.save(user);

            throw new GlobalExceptionHandler.UnauthorizedException("Invalid email or password");
        }

        // ✅ Successful login → reset security counters
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);

        AuthResponseDto response = new AuthResponseDto();
        response.accessToken = jwtUtil.generateToken(user);
        response.refreshToken = UUID.randomUUID().toString();

        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new GlobalExceptionHandler.UnauthorizedException("Token missing");
        }

        String token = authHeader.substring(7);
        LocalDateTime expiry = jwtUtil.extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiryTime(expiry);

        blacklistedTokenRepository.save(blacklistedToken);

        return ResponseEntity.ok("Logged out successfully");
    }

}
