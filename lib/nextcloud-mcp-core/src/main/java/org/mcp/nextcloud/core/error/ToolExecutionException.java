package org.mcp.nextcloud.core.error;

public class ToolExecutionException extends NextcloudMcpException {
    public ToolExecutionException(String code, String message) {
        super(code, message);
    }

    public ToolExecutionException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
