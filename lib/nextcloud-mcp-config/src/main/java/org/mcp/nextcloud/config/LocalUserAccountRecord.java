package org.mcp.nextcloud.config;

import java.util.List;

public record LocalUserAccountRecord(
        String accountKey,
        String accountId,
        String baseUrl,
        String username,
        String appPassword,
        String displayName,
        String email,
        boolean defaultAccount,
        boolean admin,
        boolean enabled,
        List<String> scopes) {
    public LocalUserAccountRecord {
        scopes = scopes == null ? List.of() : List.copyOf(scopes);
    }

    public NextcloudAccountConfig accountConfig() {
        return new NextcloudAccountConfig(
                accountId,
                baseUrl,
                username,
                appPassword,
                defaultAccount,
                admin,
                enabled,
                scopes);
    }
}
