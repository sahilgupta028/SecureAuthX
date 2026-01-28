package com.example.roleAuthentication.repository;

import com.example.roleAuthentication.entity.AuditLog;
import com.example.roleAuthentication.model.AuditAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByUsername(String username);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByTimestampBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}

