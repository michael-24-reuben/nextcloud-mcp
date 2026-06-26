package org.mcp.nextcloud.http;

import java.time.Duration;

public record RetryPolicy(int maxAttempts, Duration delay) {
    public static RetryPolicy none() {
        return new RetryPolicy(1, Duration.ZERO);
    }

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        delay = delay == null ? Duration.ZERO : delay;
    }
}
