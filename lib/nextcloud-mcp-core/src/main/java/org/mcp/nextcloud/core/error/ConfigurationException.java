package org.mcp.nextcloud.core.error;

public class ConfigurationException extends NextcloudMcpException {
    public ConfigurationException(String code, String message) {
        super(code, message);
    }

    public ConfigurationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
