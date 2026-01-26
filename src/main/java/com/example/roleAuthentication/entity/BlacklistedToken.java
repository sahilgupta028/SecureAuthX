package com.example.roleAuthentication.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "blacklistedToken")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BlacklistedToken {
    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String token;

    private LocalDateTime expiryTime;

    private LocalDateTime blacklistedAt = LocalDateTime.now();
}
