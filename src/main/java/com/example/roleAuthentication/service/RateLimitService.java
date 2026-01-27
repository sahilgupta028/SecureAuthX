package com.example.roleAuthentication.service;

import com.example.roleAuthentication.model.RateLimit;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.roleAuthentication.constants.RateLimitConstants.MAX_REQUESTS;
import static com.example.roleAuthentication.constants.RateLimitConstants.WINDOW_TIME;

@Service
public class RateLimitService {
    private final Map<String, RateLimit> cache = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        long now = System.currentTimeMillis();

        RateLimit rateLimit = cache.get(key);

        if (rateLimit == null) {
            cache.put(key, new RateLimit(1, now));
            return true;
        }

        if (now - rateLimit.getWindowStart() > WINDOW_TIME) {
            rateLimit.reset(now);
            return true;
        }

        if (rateLimit.getRequests() < MAX_REQUESTS) {
            rateLimit.increment();
            return true;
        }

        return false;
    }
}
