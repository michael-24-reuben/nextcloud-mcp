package org.mcp.nextcloud.security;

import java.util.Set;

public final class ScopeEvaluator {
    public boolean allows(Set<Scope> granted, Set<Scope> required) {
        Set<Scope> safeGranted = granted == null ? Set.of() : granted;
        Set<Scope> safeRequired = required == null ? Set.of() : required;
        return safeGranted.containsAll(safeRequired);
    }

    public Set<Scope> expand(Set<Scope> scopes) {
        Set<Scope> expanded = new java.util.LinkedHashSet<>();

        if (scopes == null || scopes.isEmpty()) {
            return expanded;
        }

        for (Scope scope : scopes) {
            expanded.add(scope);
            expanded.addAll(scope.flattenedPrerequisites());
        }

        return java.util.Collections.unmodifiableSet(expanded);
    }

}
