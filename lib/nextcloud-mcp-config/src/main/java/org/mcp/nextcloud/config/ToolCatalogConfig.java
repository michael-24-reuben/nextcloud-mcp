package org.mcp.nextcloud.config;

import java.util.Map;

public record ToolCatalogConfig(Map<String, ToolPolicyConfig> tools) {
    public ToolCatalogConfig {
        tools = tools == null ? Map.of() : Map.copyOf(tools);
    }
}
