package com.example.roleAuthentication.service;

import com.example.roleAuthentication.dto.AdminDashboardResponseDto;
import com.example.roleAuthentication.dto.UserSummaryResponseDto;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public List<UserSummaryResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserSummaryResponseDto dto = new UserSummaryResponseDto();
                    dto.setId(String.valueOf(user.getId()));
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
}

