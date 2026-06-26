package org.mcp.nextcloud.config;

import java.util.Map;

public record NextcloudMcpConfig(
        ServerConfig server,
        Map<String, NextcloudAccountConfig> accounts,
        ToolCatalogConfig tools,
        NextcloudAdminConfig admin) {
    public NextcloudMcpConfig {
        server = server == null ? ServerConfig.defaults() : server;
        accounts = accounts == null ? Map.of() : Map.copyOf(accounts);
        tools = tools == null ? new ToolCatalogConfig(Map.of()) : tools;
        admin = admin == null ? NextcloudAdminConfig.disabled() : admin;
    }
}
