package org.mcp.nextcloud.config;

import java.util.List;

public record ToolPolicyConfig(boolean enabled, List<String> requiredScopes, boolean destructive) {
    public ToolPolicyConfig {
        requiredScopes = requiredScopes == null ? List.of() : List.copyOf(requiredScopes);
    }
}
