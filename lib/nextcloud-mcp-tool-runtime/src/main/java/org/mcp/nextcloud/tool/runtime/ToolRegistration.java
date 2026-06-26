package org.mcp.nextcloud.tool.runtime;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolHandler;

public record ToolRegistration(ToolDescriptor descriptor, ToolHandler handler) {
    public ToolRegistration {
        descriptor = Preconditions.requireNonNull(descriptor, "tool descriptor");
        handler = Preconditions.requireNonNull(handler, "tool handler");
    }
}
