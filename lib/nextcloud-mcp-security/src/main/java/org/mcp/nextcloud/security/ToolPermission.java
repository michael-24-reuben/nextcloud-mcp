package org.mcp.nextcloud.security;

import java.util.Set;

import org.mcp.nextcloud.core.id.ToolId;

public record ToolPermission(ToolId toolId, Set<Scope> requiredScopes, boolean destructive) {
    public ToolPermission {
        requiredScopes = requiredScopes == null ? Set.of() : Set.copyOf(requiredScopes);
    }
}
