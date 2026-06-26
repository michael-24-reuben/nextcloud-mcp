package org.mcp.nextcloud.config;

public record NextcloudAdminConfig(boolean enabled, String accountId) {
    public static NextcloudAdminConfig disabled() {
        return new NextcloudAdminConfig(false, null);
    }
}
