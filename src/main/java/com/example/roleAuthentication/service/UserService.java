package com.example.roleAuthentication.service;

import com.example.roleAuthentication.dto.UserProfileResponseDto;
import com.example.roleAuthentication.entity.User;
import com.example.roleAuthentication.exception.GlobalExceptionHandler;
import com.example.roleAuthentication.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponseDto getUserProfile() {

        String currentUserEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new GlobalExceptionHandler.ResourceNotFoundException("User not found"));

        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());

        return response;
    }
}

