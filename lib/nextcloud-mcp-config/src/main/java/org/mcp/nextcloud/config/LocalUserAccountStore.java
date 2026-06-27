package org.mcp.nextcloud.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LocalUserAccountStore {
    public static final Path DEFAULT_USER_DIRECTORY = Path.of("config", "db", "u");

    private LocalUserAccountStore() {
    }

    public static NextcloudMcpConfig mergeUserAccounts(NextcloudMcpConfig config, Path configPath) throws IOException {
        Map<String, NextcloudAccountConfig> accounts = new LinkedHashMap<>(config.accounts());
        for (LocalUserAccountRecord record : LocalUserAccountRepository.list(configPath)) {
            accounts.put(record.accountKey(), record.accountConfig());
        }
        return new NextcloudMcpConfig(config.server(), accounts, config.tools(), resolvedAdmin(config.admin(), accounts));
    }

    private static NextcloudAdminConfig resolvedAdmin(
            NextcloudAdminConfig admin,
            Map<String, NextcloudAccountConfig> accounts) {
        if (admin.enabled() && admin.accountId() != null && !admin.accountId().isBlank()) {
            return admin;
        }
        return accounts.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().admin() && entry.getValue().enabled())
                .map(entry -> new NextcloudAdminConfig(true, entry.getKey()))
                .findFirst()
                .orElse(admin);
    }
}
