package com.example.roleAuthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdminDashboardResponseDto {
    private long totalUsers;
    private long activeUsers;
    private long lockedUsers;
}
