package com.example.roleAuthentication.controller;

import com.example.roleAuthentication.dto.AdminDashboardResponseDto;
import com.example.roleAuthentication.dto.UserSummaryResponseDto;
import com.example.roleAuthentication.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Admin dashboard summary
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponseDto> dashboard() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    /**
     * Fetch all users (Admin only)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Lock or unlock a user account
     */
    @PutMapping("/users/{userId}/lock")
    public ResponseEntity<String> lockUser(@PathVariable String userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok("User account locked successfully");
    }

    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable String userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok("User account unlocked successfully");
    }
}