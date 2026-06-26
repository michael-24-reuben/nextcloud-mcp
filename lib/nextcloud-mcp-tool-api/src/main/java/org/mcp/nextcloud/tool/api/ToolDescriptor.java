package org.mcp.nextcloud.tool.api;

import java.util.Map;

import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;

public record ToolDescriptor(
        ToolId id,
        String name,
        String description,
        ToolInputSchema inputSchema,
        ToolOutputSchema outputSchema,
        ToolSecurity security,
        Map<String, Object> metadata) {
    public ToolDescriptor {
        id = Preconditions.requireNonNull(id, "tool id");
        name = Preconditions.requireNonBlank(name, "tool name");
        description = description == null ? "" : description.trim();
        inputSchema = inputSchema == null ? ToolInputSchema.empty() : inputSchema;
        outputSchema = outputSchema == null ? ToolOutputSchema.object() : outputSchema;
        security = security == null ? ToolSecurity.none() : security;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
