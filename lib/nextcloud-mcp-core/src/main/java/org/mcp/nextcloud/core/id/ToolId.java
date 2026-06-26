package org.mcp.nextcloud.core.id;

import org.mcp.nextcloud.core.util.Preconditions;

public record ToolId(String value) {
    public ToolId {
        value = Preconditions.requireNonBlank(value, "tool id");
    }
}
