package org.mcp.nextcloud.tool.api;

@FunctionalInterface
public interface ToolHandler {
    ToolResult invoke(ToolInvocation invocation) throws Exception;
}
