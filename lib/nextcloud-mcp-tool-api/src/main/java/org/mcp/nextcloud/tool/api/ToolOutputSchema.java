package org.mcp.nextcloud.tool.api;

import java.util.List;
import java.util.Map;

public record ToolOutputSchema(List<ToolValueType> contentTypes, Map<String, Object> metadata) {
    public ToolOutputSchema {
        contentTypes = contentTypes == null ? List.of(ToolValueType.OBJECT) : List.copyOf(contentTypes);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ToolOutputSchema object() {
        return new ToolOutputSchema(List.of(ToolValueType.OBJECT), Map.of());
    }
}
