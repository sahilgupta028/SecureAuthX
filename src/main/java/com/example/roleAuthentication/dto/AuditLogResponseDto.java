package com.example.roleAuthentication.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class AuditLogResponseDto {

    private String id;
    private String username;
    private String action;
    private String ipAddress;
    private String endpoint;
    private LocalDateTime timestamp;

}