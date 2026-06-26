package org.mcp.nextcloud.tool.api;

import java.util.Map;

import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;

public record ToolInvocation(ToolId toolId, ToolInvocationContext context, Map<String, Object> arguments) {
    public ToolInvocation {
        toolId = Preconditions.requireNonNull(toolId, "tool id");
        context = Preconditions.requireNonNull(context, "invocation context");
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }
}
