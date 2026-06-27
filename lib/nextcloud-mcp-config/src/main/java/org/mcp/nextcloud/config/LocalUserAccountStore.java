package org.mcp.nextcloud.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class LocalUserAccountStore {
    public static final Path DEFAULT_USER_DIRECTORY = Path.of("config", "db", "u");

    private LocalUserAccountStore() {
    }

    public static NextcloudMcpConfig mergeUserAccounts(NextcloudMcpConfig config, Path configPath) throws IOException {
        Path userDirectory = userDirectory(configPath);
        Map<String, NextcloudAccountConfig> accounts = new LinkedHashMap<>(config.accounts());
        if (Files.isDirectory(userDirectory)) {
            for (Path path : userFiles(userDirectory)) {
                Optional<Map.Entry<String, NextcloudAccountConfig>> account = loadAccount(path);
                account.ifPresent(entry -> accounts.put(entry.getKey(), entry.getValue()));
            }
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

    private static Optional<Map.Entry<String, NextcloudAccountConfig>> loadAccount(Path path) throws IOException {
        Map<String, String> values = EnvFile.read(path);
        if (values.isEmpty() || bool(values, "ENABLED", true).isEmpty()) {
            return Optional.empty();
        }
        String fileKey = accountKeyFromFile(path);
        String accountKey = value(values, "ACCOUNT_KEY").orElse(fileKey);
        String accountName = value(values, "ACCOUNT_NAME").orElse(fileKey);
        String baseUrl = value(values, "BASE_URL").orElse(null);
        String appPassword = value(values, "APP_PASSWORD")
                .or(() -> value(values, "APP_PASS"))
                .orElse(null);
        boolean defaultAccount = bool(values, "DEFAULT_ACCOUNT", false).orElse(false);
        boolean admin = bool(values, "ADMIN", false).orElse(false);
        boolean enabled = bool(values, "ENABLED", true).orElse(true);
        List<String> scopes = csv(values.get("SCOPES"));
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                accountName,
                baseUrl,
                accountName,
                appPassword,
                defaultAccount,
                admin,
                enabled,
                scopes);
        return Optional.of(Map.entry(accountKey, account));
    }

    private static Path userDirectory(Path configPath) {
        if (configPath == null) {
            return DEFAULT_USER_DIRECTORY;
        }
        Path root = configPath.toAbsolutePath().normalize().getParent();
        if (root == null) {
            return DEFAULT_USER_DIRECTORY;
        }
        return root.resolve("db").resolve("u");
    }

    private static List<Path> userFiles(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.startsWith("usr-") && name.endsWith(".env");
                    })
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }

    private static String accountKeyFromFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring("usr-".length(), fileName.length() - ".env".length());
    }

    private static Optional<String> value(Map<String, String> values, String key) {
        String value = values.get(key);
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    private static Optional<Boolean> bool(Map<String, String> values, String key, boolean defaultValue) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            return Optional.of(defaultValue);
        }
        return Optional.of(switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "1", "true", "yes", "y", "on" -> true;
            case "0", "false", "no", "n", "off" -> false;
            default -> defaultValue;
        });
    }

    private static List<String> csv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (String item : value.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isBlank()) {
                values.add(trimmed);
            }
        }
        return List.copyOf(values);
    }
}
