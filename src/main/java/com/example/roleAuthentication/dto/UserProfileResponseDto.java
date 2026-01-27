package com.example.roleAuthentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileResponseDto {
    private String name;
    private String email;
    private String role;
}
