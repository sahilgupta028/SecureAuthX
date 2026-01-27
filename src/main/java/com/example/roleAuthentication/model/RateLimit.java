package com.example.roleAuthentication.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimit {
    private int requests;
    private long windowStart;

    public RateLimit(int requests, long windowStart) {
        this.requests = requests;
        this.windowStart = windowStart;
    }

    public void increment() {
        this.requests++;
    }

    public void reset(long now) {
        this.requests = 1;
        this.windowStart = now;
    }
}
