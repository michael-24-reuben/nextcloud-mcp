package org.mcp.nextcloud.client;

import org.mcp.nextcloud.core.error.NextcloudMcpException;

public class NextcloudClientException extends NextcloudMcpException {
    public NextcloudClientException(String code, String message) {
        super(code, message);
    }

    public NextcloudClientException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
