package org.mcp.nextcloud.security;

import org.mcp.nextcloud.security.Scopes.Admin;

import java.util.Set;

public final class ToolAccessPolicy {
    private final ScopeEvaluator evaluator;
    private final boolean denyDeleteByDefault;

    public ToolAccessPolicy() {
        this(new ScopeEvaluator(), true);
    }

    public ToolAccessPolicy(ScopeEvaluator evaluator, boolean denyDeleteByDefault) {
        this.evaluator = evaluator;
        this.denyDeleteByDefault = denyDeleteByDefault;
    }

    public boolean canInvoke(Principal principal, ToolPermission permission) {
        if (principal == null || permission == null) {
            return false;
        }

        Set<Scope> requiredScopes = evaluator.expand(permission.requiredScopes());

        boolean adminOperation = requiredScopes.stream().anyMatch(Scope::adminScope);
        if (adminOperation && !principal.admin()) {
            return false;
        }

        if (!evaluator.allows(principal.scopes(), requiredScopes)) {
            return false;
        }

        if (denyDeleteByDefault && permission.destructive()) {

            // Returns whether it has risk gate
            return principal.scopes().contains(Scopes.Risk.DESTRUCTIVE)
                    || principal.scopes().contains(Scopes.Risk.CRITICAL);
        }

        return true;
    }
}
