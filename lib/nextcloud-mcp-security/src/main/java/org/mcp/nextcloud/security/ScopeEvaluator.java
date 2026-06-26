package org.mcp.nextcloud.security;

import java.util.Set;

public final class ScopeEvaluator {
    public boolean allows(Set<Scope> granted, Set<Scope> required) {
        Set<Scope> safeGranted = granted == null ? Set.of() : granted;
        Set<Scope> safeRequired = required == null ? Set.of() : required;
        return safeGranted.containsAll(safeRequired);
    }
}
