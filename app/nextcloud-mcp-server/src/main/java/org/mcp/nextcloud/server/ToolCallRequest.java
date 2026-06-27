package org.mcp.nextcloud.server;

import java.util.Map;

public record ToolCallRequest(
        String tool,
        String accountId,
        String invocationId,
        Map<String, Object> arguments) {
    public ToolCallRequest {
        arguments = arguments == null ? Map.of() : Map.copyOf(arguments);
    }
}
