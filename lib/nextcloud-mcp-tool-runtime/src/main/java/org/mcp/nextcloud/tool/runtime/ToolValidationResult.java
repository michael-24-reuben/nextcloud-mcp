package org.mcp.nextcloud.tool.runtime;

import java.util.List;

public record ToolValidationResult(boolean valid, List<String> errors) {
    public ToolValidationResult {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public static ToolValidationResult ok() {
        return new ToolValidationResult(true, List.of());
    }

    public static ToolValidationResult failed(List<String> errors) {
        return new ToolValidationResult(false, errors);
    }
}
