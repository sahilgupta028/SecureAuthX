package com.example.roleAuthentication.filter;

import com.example.roleAuthentication.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Apply ONLY on auth endpoints
        if (path.startsWith("/api/auth/login")) {

            String ip = request.getRemoteAddr();

            if (!rateLimitService.isAllowed(ip)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("""
                {
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Too many login attempts. Please try again later."
                }
                """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}