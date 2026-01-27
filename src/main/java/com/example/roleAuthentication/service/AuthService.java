package com.example.roleAuthentication.service;

import com.example.roleAuthentication.dto.AuthResponseDto;
import com.example.roleAuthentication.dto.LoginRequestDto;
import com.example.roleAuthentication.dto.RegisterRequestDto;
import com.example.roleAuthentication.entity.BlacklistedToken;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.model.Role;
import com.example.roleAuthentication.repository.BlacklistedTokenRepository;
import com.example.roleAuthentication.repository.UserRepository;
import com.example.roleAuthentication.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.example.roleAuthentication.model.SecurityConstants.LOCK_TIME_MINUTES;
import static com.example.roleAuthentication.model.SecurityConstants.MAX_FAILED_ATTEMPTS;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            BlacklistedTokenRepository blacklistedTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequestDto req) {

        if (userRepository.findByEmail(req.email).isPresent()) {
            throw new GlobalExceptionHandler.BadRequestException("Email already exists");
        }

        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setRole(req.role == null ? Role.ROLE_USER : req.role);

        userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto req) {

        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        // 🔒 Account lock check
        if (user.isAccountLocked()) {

            if (user.getLockTime() != null &&
                    user.getLockTime()
                            .plusMinutes(LOCK_TIME_MINUTES)
                            .isBefore(LocalDateTime.now())) {

                unlockAccount(user);
            } else {
                throw new GlobalExceptionHandler.UnauthorizedException(
                        "Account locked due to multiple failed attempts. Try again later."
                );
            }
        }

        // ❌ Invalid password
        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            handleFailedAttempt(user);
            throw new GlobalExceptionHandler.UnauthorizedException("Invalid email or password");
        }

        // ✅ Successful login
        resetSecurityCounters(user);

        AuthResponseDto response = new AuthResponseDto();
        response.accessToken = jwtUtil.generateToken(user);
        response.refreshToken = UUID.randomUUID().toString();

        return response;
    }

    public void logout(HttpServletRequest request) {

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
    }

    /* ===================== PRIVATE HELPERS ===================== */

    private void handleFailedAttempt(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
        }

        userRepository.save(user);
    }

    private void resetSecurityCounters(User user) {
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }

    private void unlockAccount(User user) {
        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }
}