package org.mcp.nextcloud.tool.runtime;

import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.security.PrincipalContext;

public record ToolRuntimeContext(PrincipalContext principalContext, Map<String, Object> attributes) {
    public ToolRuntimeContext {
        principalContext = Preconditions.requireNonNull(principalContext, "principal context");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
