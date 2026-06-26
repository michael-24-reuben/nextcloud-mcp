package org.mcp.nextcloud.http;

import java.time.Duration;

public record RateLimitPolicy(int permits, Duration window) {
    public static RateLimitPolicy unbounded() {
        return new RateLimitPolicy(Integer.MAX_VALUE, Duration.ZERO);
    }

    public RateLimitPolicy {
        if (permits < 1) {
            throw new IllegalArgumentException("permits must be at least 1");
        }
        window = window == null ? Duration.ZERO : window;
    }
}
