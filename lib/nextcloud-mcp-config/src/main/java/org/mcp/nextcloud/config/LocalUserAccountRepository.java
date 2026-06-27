package org.mcp.nextcloud.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class LocalUserAccountRepository {
    private static final String FILE_PREFIX = "usr-";
    private static final String FILE_SUFFIX = ".env";

    private LocalUserAccountRepository() {
    }

    public static Path userDirectory(Path configPath) {
        if (configPath == null) {
            return LocalUserAccountStore.DEFAULT_USER_DIRECTORY;
        }
        Path root = configPath.toAbsolutePath().normalize().getParent();
        if (root == null) {
            return LocalUserAccountStore.DEFAULT_USER_DIRECTORY;
        }
        return root.resolve("db").resolve("u");
    }

    public static List<LocalUserAccountRecord> list(Path configPath) throws IOException {
        Path directory = userDirectory(configPath);
        if (!Files.isDirectory(directory)) {
            return List.of();
        }
        List<LocalUserAccountRecord> records = new ArrayList<>();
        for (Path path : userFiles(directory)) {
            read(path).ifPresent(records::add);
        }
        return List.copyOf(records);
    }

    public static Optional<LocalUserAccountRecord> find(Path configPath, String accountKey) throws IOException {
        Path path = accountPath(configPath, accountKey);
        return read(path);
    }

    public static LocalUserAccountRecord create(Path configPath, LocalUserAccountRecord record) throws IOException {
        Path path = accountPath(configPath, record.accountKey());
        if (Files.exists(path)) {
            throw new IllegalStateException("account already exists: " + record.accountKey());
        }
        return write(configPath, record);
    }

    public static LocalUserAccountRecord write(Path configPath, LocalUserAccountRecord record) throws IOException {
        Path path = accountPath(configPath, record.accountKey());
        Files.createDirectories(path.getParent());
        if (record.defaultAccount()) {
            clearDefaultFlags(configPath, record.accountKey());
        }
        Files.writeString(path, render(record), StandardCharsets.UTF_8);
        return record;
    }

    public static boolean delete(Path configPath, String accountKey) throws IOException {
        return Files.deleteIfExists(accountPath(configPath, accountKey));
    }

    public static LocalUserAccountRecord makeDefault(Path configPath, String accountKey) throws IOException {
        LocalUserAccountRecord current = find(configPath, accountKey)
                .orElseThrow(() -> new IllegalArgumentException("account not found: " + accountKey));
        LocalUserAccountRecord updated = new LocalUserAccountRecord(
                current.accountKey(),
                current.accountId(),
                current.baseUrl(),
                current.username(),
                current.appPassword(),
                current.displayName(),
                current.email(),
                true,
                current.admin(),
                current.enabled(),
                current.scopes());
        return write(configPath, updated);
    }

    public static Optional<LocalUserAccountRecord> read(Path path) throws IOException {
        Map<String, String> values = EnvFile.read(path);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        String fileKey = accountKeyFromFile(path);
        String accountKey = value(values, "ACCOUNT_KEY").orElse(fileKey);
        String accountId = value(values, "ACCOUNT_ID")
                .or(() -> value(values, "ACCOUNT_NAME"))
                .orElse(fileKey);
        String username = value(values, "USERNAME")
                .or(() -> value(values, "LOGIN_NAME"))
                .or(() -> value(values, "LOGIN"))
                .orElse(accountId);
        String appPassword = value(values, "APP_PASSWORD")
                .or(() -> value(values, "APP_PASS"))
                .orElse(null);
        return Optional.of(new LocalUserAccountRecord(
                accountKey,
                accountId,
                value(values, "BASE_URL").orElse(null),
                username,
                appPassword,
                value(values, "DISPLAY_NAME").orElse(null),
                value(values, "EMAIL").orElse(null),
                bool(values, "DEFAULT_ACCOUNT", false),
                bool(values, "ADMIN", false),
                bool(values, "ENABLED", true),
                csv(values.get("SCOPES"))));
    }

    private static void clearDefaultFlags(Path configPath, String exceptAccountKey) throws IOException {
        Path directory = userDirectory(configPath);
        if (!Files.isDirectory(directory)) {
            return;
        }
        for (Path path : userFiles(directory)) {
            Optional<LocalUserAccountRecord> existing = read(path);
            if (existing.isEmpty() || existing.get().accountKey().equals(exceptAccountKey) || !existing.get().defaultAccount()) {
                continue;
            }
            LocalUserAccountRecord current = existing.get();
            write(configPath, new LocalUserAccountRecord(
                    current.accountKey(),
                    current.accountId(),
                    current.baseUrl(),
                    current.username(),
                    current.appPassword(),
                    current.displayName(),
                    current.email(),
                    false,
                    current.admin(),
                    current.enabled(),
                    current.scopes()));
        }
    }

    private static Path accountPath(Path configPath, String accountKey) {
        String cleanKey = requireAccountKey(accountKey);
        return userDirectory(configPath).resolve(FILE_PREFIX + cleanKey + FILE_SUFFIX);
    }

    private static List<Path> userFiles(Path directory) throws IOException {
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.startsWith(FILE_PREFIX) && name.endsWith(FILE_SUFFIX);
                    })
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }

    private static String render(LocalUserAccountRecord record) {
        Map<String, String> values = new LinkedHashMap<>();
        put(values, "ACCOUNT_KEY", requireAccountKey(record.accountKey()));
        put(values, "ACCOUNT_ID", record.accountId());
        put(values, "BASE_URL", record.baseUrl());
        put(values, "USERNAME", record.username());
        put(values, "APP_PASSWORD", record.appPassword());
        put(values, "DISPLAY_NAME", record.displayName());
        put(values, "EMAIL", record.email());
        values.put("DEFAULT_ACCOUNT", Boolean.toString(record.defaultAccount()));
        values.put("ADMIN", Boolean.toString(record.admin()));
        values.put("ENABLED", Boolean.toString(record.enabled()));
        values.put("SCOPES", String.join(",", record.scopes()));
        StringBuilder builder = new StringBuilder();
        values.forEach((key, value) -> builder.append(key)
                .append('=')
                .append(quote(value))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    private static void put(Map<String, String> values, String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    private static String quote(String value) {
        if (value.isBlank() || value.chars().anyMatch(ch -> Character.isWhitespace(ch) || ch == '#' || ch == '"' || ch == '\'')) {
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        return value;
    }

    private static String accountKeyFromFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(FILE_PREFIX.length(), fileName.length() - FILE_SUFFIX.length());
    }

    private static String requireAccountKey(String accountKey) {
        if (accountKey == null || accountKey.isBlank()) {
            throw new IllegalArgumentException("account key is required");
        }
        String cleanKey = accountKey.trim();
        if (!cleanKey.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("account key contains unsupported characters: " + accountKey);
        }
        return cleanKey;
    }

    private static Optional<String> value(Map<String, String> values, String key) {
        String value = values.get(key);
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    private static boolean bool(Map<String, String> values, String key, boolean defaultValue) {
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "1", "true", "yes", "y", "on" -> true;
            case "0", "false", "no", "n", "off" -> false;
            default -> defaultValue;
        };
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
