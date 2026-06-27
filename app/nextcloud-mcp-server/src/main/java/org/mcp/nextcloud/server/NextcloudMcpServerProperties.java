package org.mcp.nextcloud.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nextcloud.mcp")
public record NextcloudMcpServerProperties(
        String configPath,
        String defaultAccountId,
        boolean validateOnStartup) {
    public NextcloudMcpServerProperties {
        configPath = blankToNull(configPath);
        defaultAccountId = blankToNull(defaultAccountId);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
