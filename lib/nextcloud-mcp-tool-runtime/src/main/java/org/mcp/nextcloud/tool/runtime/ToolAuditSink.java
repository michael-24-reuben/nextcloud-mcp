package org.mcp.nextcloud.tool.runtime;

import org.mcp.nextcloud.security.AuditEvent;

@FunctionalInterface
public interface ToolAuditSink {
    void record(AuditEvent event);

    static ToolAuditSink noop() {
        return event -> {
        };
    }
}
