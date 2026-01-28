package com.example.roleAuthentication.service;

import com.example.roleAuthentication.dto.AdminDashboardResponseDto;
import com.example.roleAuthentication.dto.AuditLogResponseDto;
import com.example.roleAuthentication.dto.UserSummaryResponseDto;
import com.example.roleAuthentication.entity.AuditLog;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.model.AuditAction;
import com.example.roleAuthentication.repository.AuditLogRepository;
import com.example.roleAuthentication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminService(
            UserRepository userRepository,
            AuditLogRepository auditLogRepository
    ) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /* ===================== DASHBOARD ===================== */

    public AdminDashboardResponseDto getDashboardData() {

        long totalUsers = userRepository.count();
        long lockedUsers = userRepository.countByAccountLocked(true);
        long activeUsers = totalUsers - lockedUsers;

        AdminDashboardResponseDto response = new AdminDashboardResponseDto();
        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        response.setLockedUsers(lockedUsers);

        return response;
    }

    /* ===================== USERS ===================== */

    public List<UserSummaryResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserSummaryResponseDto dto = new UserSummaryResponseDto();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole().name());
                    dto.setAccountLocked(user.isAccountLocked());
                    return dto;
                })
                .toList();
    }

    public void lockUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        user.setAccountLocked(true);
        userRepository.save(user);
    }

    public void unlockUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        user.setLockTime(null);

        userRepository.save(user);
    }

    /* ===================== AUDIT LOGS ===================== */

    public List<AuditLogResponseDto> getAllAuditLogs() {

        return auditLogRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<AuditLogResponseDto> getAuditLogsByUser(String username) {

        return auditLogRepository.findByUsername(username)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<AuditLogResponseDto> getAuditLogsByAction(AuditAction action) {

        return auditLogRepository.findByAction(action)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /* ===================== MAPPER ===================== */

    private AuditLogResponseDto mapToDto(AuditLog log) {

        AuditLogResponseDto dto = new AuditLogResponseDto();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setAction(log.getAction());
        dto.setIpAddress(log.getIpAddress());
        dto.setEndpoint(log.getEndpoint());
        dto.setTimestamp(log.getTimestamp());

        return dto;
    }
}