package org.mcp.nextcloud.tool.api;

import java.util.Map;

import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.core.util.Preconditions;

public record ToolInvocationContext(
        InvocationId invocationId,
        AccountId accountId,
        String principalId,
        Map<String, Object> attributes) {
    public ToolInvocationContext {
        invocationId = Preconditions.requireNonNull(invocationId, "invocation id");
        principalId = principalId == null ? "" : principalId.trim();
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
