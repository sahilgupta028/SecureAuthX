package com.example.roleAuthentication.entity;

import com.example.roleAuthentication.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    @Id
    private String id;

    private String name;

    @Email
    @Indexed(unique = true)
    private String email;

    @Size(min = 6)
    private String password;

    private Role role;

    private int failedAttempts;

    private boolean accountLocked = false;

    private LocalDateTime lockTime;

    private boolean active = true;
}
