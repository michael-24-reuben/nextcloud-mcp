package org.mcp.nextcloud.tool.api;

import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record ToolContent(String type, Object value, Map<String, Object> metadata) {
    public ToolContent {
        type = Preconditions.requireNonBlank(type, "content type");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ToolContent text(String text) {
        return new ToolContent("text", text == null ? "" : text, Map.of());
    }

    public static ToolContent json(Object value) {
        return new ToolContent("json", value, Map.of());
    }
}
