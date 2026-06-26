package org.mcp.nextcloud.core.error;

public class NextcloudMcpException extends RuntimeException {
    private final String code;

    public NextcloudMcpException(String code, String message) {
        super(message);
        this.code = code;
    }

    public NextcloudMcpException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
