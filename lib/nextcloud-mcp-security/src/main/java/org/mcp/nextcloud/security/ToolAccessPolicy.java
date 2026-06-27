package org.mcp.nextcloud.security;

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
        if (!principal.admin() && permission.requiredScopes().stream().anyMatch(Scope::adminScope)) {
            return false;
        }
        boolean adminOperation = permission.requiredScopes().stream().anyMatch(Scope::adminScope);
        if (denyDeleteByDefault && permission.destructive() && !adminOperation && !principal.scopes().contains(Scope.FILES_DELETE)) {
            return false;
        }
        return evaluator.allows(principal.scopes(), permission.requiredScopes());
    }
}
