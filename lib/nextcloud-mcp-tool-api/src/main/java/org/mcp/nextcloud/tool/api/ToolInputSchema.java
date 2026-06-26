package org.mcp.nextcloud.tool.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ToolInputSchema(List<ToolParameter> parameters, boolean additionalProperties) {
    public ToolInputSchema {
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
        Map<String, ToolParameter> byName = new LinkedHashMap<>();
        for (ToolParameter parameter : parameters) {
            if (byName.put(parameter.name(), parameter) != null) {
                throw new IllegalArgumentException("duplicate parameter: " + parameter.name());
            }
        }
    }

    public static ToolInputSchema empty() {
        return new ToolInputSchema(List.of(), false);
    }

    public Map<String, ToolParameter> parametersByName() {
        Map<String, ToolParameter> byName = new LinkedHashMap<>();
        for (ToolParameter parameter : parameters) {
            byName.put(parameter.name(), parameter);
        }
        return Map.copyOf(byName);
    }
}
