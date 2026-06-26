package org.mcp.nextcloud.tool.runtime;

import java.util.List;
import java.util.Optional;

import org.mcp.nextcloud.core.id.ToolId;

public interface ToolRegistry {
    ToolRegistration register(ToolRegistration registration);

    Optional<ToolRegistration> find(ToolId toolId);

    List<ToolRegistration> list();
}
