package org.mcp.nextcloud.core.error;

public class NextcloudApiException extends NextcloudMcpException {
    private final int statusCode;

    public NextcloudApiException(String code, String message, int statusCode) {
        super(code, message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}
