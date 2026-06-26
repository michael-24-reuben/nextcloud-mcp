package org.mcp.nextcloud.tool.runtime;

import java.util.Set;
import java.util.stream.Collectors;

import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.security.Scope;
import org.mcp.nextcloud.security.ToolAccessPolicy;
import org.mcp.nextcloud.security.ToolPermission;
import org.mcp.nextcloud.tool.api.ToolDescriptor;

public final class DefaultToolPolicyInterceptor implements ToolPolicyInterceptor {
    private final ToolAccessPolicy accessPolicy;

    public DefaultToolPolicyInterceptor() {
        this(new ToolAccessPolicy());
    }

    public DefaultToolPolicyInterceptor(ToolAccessPolicy accessPolicy) {
        this.accessPolicy = Preconditions.requireNonNull(accessPolicy, "tool access policy");
    }

    @Override
    public ToolPolicyDecision evaluate(ToolDescriptor descriptor, PrincipalContext principalContext) {
        if (principalContext == null || principalContext.principal() == null) {
            return ToolPolicyDecision.deny("missing principal");
        }
        ToolId toolId = descriptor.id();
        Set<Scope> scopes = descriptor.security().requiredScopes().stream()
                .map(Scope::new)
                .collect(Collectors.toUnmodifiableSet());
        ToolPermission permission = new ToolPermission(toolId, scopes, descriptor.security().destructive());
        if (!accessPolicy.canInvoke(principalContext.principal(), permission)) {
            return ToolPolicyDecision.deny("principal is not allowed to invoke " + toolId.value());
        }
        return ToolPolicyDecision.allow();
    }
}
