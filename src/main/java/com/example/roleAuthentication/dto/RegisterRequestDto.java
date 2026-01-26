package com.example.roleAuthentication.dto;

import com.example.roleAuthentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequestDto {
    public String name;
    public String email;
    public String password;
    public Role role;
}