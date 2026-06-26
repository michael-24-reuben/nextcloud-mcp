package org.mcp.nextcloud.admin;

import org.mcp.nextcloud.core.error.NextcloudMcpException;

public class NextcloudAdminClientException extends NextcloudMcpException {
    public NextcloudAdminClientException(String code, String message) {
        super(code, message);
    }

    public NextcloudAdminClientException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
