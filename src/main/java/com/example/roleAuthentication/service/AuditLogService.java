package com.example.roleAuthentication.service;

import com.example.roleAuthentication.entity.AuditLog;
import com.example.roleAuthentication.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Async
    public void log(String username, String action, @NonNull HttpServletRequest request) {

        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setIpAddress(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}