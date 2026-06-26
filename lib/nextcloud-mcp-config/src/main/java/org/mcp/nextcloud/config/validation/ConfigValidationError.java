package org.mcp.nextcloud.config.validation;

public record ConfigValidationError(String path, String message) {
}
