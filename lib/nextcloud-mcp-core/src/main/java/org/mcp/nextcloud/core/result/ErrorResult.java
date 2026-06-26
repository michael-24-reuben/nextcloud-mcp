package org.mcp.nextcloud.core.result;

import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record ErrorResult(String code, String message, Map<String, Object> details) {
    public ErrorResult {
        code = Preconditions.requireNonBlank(code, "error code");
        message = Preconditions.requireNonBlank(message, "error message");
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
