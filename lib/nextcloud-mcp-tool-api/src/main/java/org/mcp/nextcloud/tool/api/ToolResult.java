package org.mcp.nextcloud.tool.api;

import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.result.ErrorResult;

public record ToolResult(
        boolean success,
        List<ToolContent> content,
        Object structuredContent,
        ErrorResult error,
        Map<String, Object> metadata) {
    public ToolResult {
        content = content == null ? List.of() : List.copyOf(content);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        if (success && error != null) {
            throw new IllegalArgumentException("successful tool result cannot include an error");
        }
        if (!success && error == null) {
            throw new IllegalArgumentException("failed tool result requires an error");
        }
    }

    public static ToolResult ok(Object structuredContent) {
        return new ToolResult(true, List.of(ToolContent.json(structuredContent)), structuredContent, null, Map.of());
    }

    public static ToolResult ok(List<ToolContent> content, Object structuredContent) {
        return new ToolResult(true, content, structuredContent, null, Map.of());
    }

    public static ToolResult failed(ErrorResult error) {
        return new ToolResult(false, List.of(), null, error, Map.of());
    }
}
