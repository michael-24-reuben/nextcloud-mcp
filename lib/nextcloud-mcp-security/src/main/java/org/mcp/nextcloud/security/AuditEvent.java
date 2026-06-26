package org.mcp.nextcloud.security;

import java.time.Instant;
import java.util.Map;

import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.core.id.ToolId;

public record AuditEvent(
        Instant occurredAt,
        InvocationId invocationId,
        ToolId toolId,
        AccountId accountId,
        String principalId,
        String outcome,
        Map<String, Object> attributes) {
    public AuditEvent {
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
