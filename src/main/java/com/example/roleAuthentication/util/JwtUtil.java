package com.example.roleAuthentication.util;

import com.example.roleAuthentication.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key =
            Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Calendar extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // same key used to generate JWT
                .build()
                .parseClaimsJws(token)
                .getBody();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(claims.getExpiration());

        return calendar;
    }

}
