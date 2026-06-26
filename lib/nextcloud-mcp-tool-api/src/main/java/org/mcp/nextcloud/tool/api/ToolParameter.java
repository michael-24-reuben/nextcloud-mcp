package org.mcp.nextcloud.tool.api;

import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record ToolParameter(
        String name,
        ToolValueType type,
        boolean required,
        String description,
        List<String> allowedValues,
        Object defaultValue,
        Map<String, Object> metadata) {
    public ToolParameter {
        name = Preconditions.requireNonBlank(name, "parameter name");
        type = Preconditions.requireNonNull(type, "parameter type");
        description = description == null ? "" : description.trim();
        allowedValues = allowedValues == null ? List.of() : List.copyOf(allowedValues);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ToolParameter required(String name, ToolValueType type, String description) {
        return new ToolParameter(name, type, true, description, List.of(), null, Map.of());
    }

    public static ToolParameter optional(String name, ToolValueType type, String description) {
        return new ToolParameter(name, type, false, description, List.of(), null, Map.of());
    }
}
