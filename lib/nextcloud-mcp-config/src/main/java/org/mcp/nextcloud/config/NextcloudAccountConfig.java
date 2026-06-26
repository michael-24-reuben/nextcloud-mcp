package org.mcp.nextcloud.config;

import java.util.List;

public record NextcloudAccountConfig(
        String id,
        String baseUrl,
        String username,
        String appPassword,
        boolean defaultAccount,
        boolean admin,
        boolean enabled,
        List<String> scopes) {
    public NextcloudAccountConfig {
        scopes = scopes == null ? List.of() : List.copyOf(scopes);
    }
}
