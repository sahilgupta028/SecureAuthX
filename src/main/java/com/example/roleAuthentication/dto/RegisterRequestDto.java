package com.example.roleAuthentication.dto;

import com.example.roleAuthentication.model.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequestDto {
    @NotBlank
    public String name;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    @Length(min = 6)
    public String password;

    public Role role;
}