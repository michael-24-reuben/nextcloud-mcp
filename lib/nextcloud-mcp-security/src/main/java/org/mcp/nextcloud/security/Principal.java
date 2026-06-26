package org.mcp.nextcloud.security;

import java.util.Set;

import org.mcp.nextcloud.core.id.PrincipalId;

public record Principal(PrincipalId id, String displayName, boolean admin, Set<Scope> scopes) {
    public Principal {
        scopes = scopes == null ? Set.of() : Set.copyOf(scopes);
    }
}
