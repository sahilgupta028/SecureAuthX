package com.example.roleAuthentication.service;

import com.example.roleAuthentication.dto.AuthResponseDto;
import com.example.roleAuthentication.dto.LoginRequestDto;
import com.example.roleAuthentication.dto.RegisterRequestDto;
import com.example.roleAuthentication.entity.BlacklistedToken;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.model.AuditAction;
import com.example.roleAuthentication.model.Role;
import com.example.roleAuthentication.repository.BlacklistedTokenRepository;
import com.example.roleAuthentication.repository.UserRepository;
import com.example.roleAuthentication.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

import static com.example.roleAuthentication.constants.SecurityConstants.LOCK_TIME_MINUTES;
import static com.example.roleAuthentication.constants.SecurityConstants.MAX_FAILED_ATTEMPTS;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    public AuthService(
            UserRepository userRepository,
            BlacklistedTokenRepository blacklistedTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.auditLogService = auditLogService;
    }

    /* ===================== REGISTER ===================== */

    public void register(RegisterRequestDto req, HttpServletRequest request) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new GlobalExceptionHandler.BadRequestException("Email already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() == null ? Role.ROLE_USER : req.getRole());
        user.setFailedAttempts(0);
        user.setAccountLocked(false);

        userRepository.save(user);

        auditLogService.log(
                user.getEmail(),
                String.valueOf(AuditAction.REGISTER_SUCCESS),
                request
        );
    }

    /* ===================== LOGIN ===================== */

    public AuthResponseDto login(LoginRequestDto req, HttpServletRequest request) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> {
                    auditLogService.log(
                            req.getEmail(),
                            String.valueOf(AuditAction.LOGIN_FAILED),
                            request
                    );
                    return new GlobalExceptionHandler.UnauthorizedException("Invalid email or password");
                });

        // 🔒 Account lock check
        if (user.isAccountLocked()) {

            if (user.getLockTime() != null &&
                    user.getLockTime()
                            .plusMinutes(LOCK_TIME_MINUTES)
                            .isBefore(LocalDateTime.now())) {

                unlockAccount(user);

            } else {
                auditLogService.log(
                        user.getEmail(),
                        String.valueOf(AuditAction.ACCOUNT_LOCKED),
                        request
                );

                throw new GlobalExceptionHandler.UnauthorizedException(
                        "Account locked due to multiple failed attempts. Try again later."
                );
            }
        }

        // ❌ Invalid password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {

            handleFailedAttempt(user);

            auditLogService.log(
                    user.getEmail(),
                    String.valueOf(AuditAction.LOGIN_FAILED),
                    request
            );

            throw new GlobalExceptionHandler.UnauthorizedException("Invalid email or password");
        }

        // ✅ Successful login
        resetSecurityCounters(user);

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(jwtUtil.generateToken(user));
        response.setRefreshToken(UUID.randomUUID().toString());

        auditLogService.log(
                user.getEmail(),
                String.valueOf(AuditAction.LOGIN_SUCCESS),
                request
        );

        return response;
    }

    /* ===================== LOGOUT ===================== */

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

        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        auditLogService.log(
                username,
                String.valueOf(AuditAction.LOGOUT),
                request
        );
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
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }
}
