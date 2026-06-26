package org.mcp.nextcloud.tool.api;

import java.util.Set;

public record ToolSecurity(Set<String> requiredScopes, boolean destructive) {
    public ToolSecurity {
        requiredScopes = requiredScopes == null ? Set.of() : Set.copyOf(requiredScopes);
    }

    public static ToolSecurity none() {
        return new ToolSecurity(Set.of(), false);
    }
}
