package org.mcp.nextcloud.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ConfigPaths {
    public static final Path DEFAULT_CONFIG_PATH = Path.of("config", "server.yaml");

    private static final List<Path> DEFAULT_CANDIDATES = List.of(
            DEFAULT_CONFIG_PATH,
            Path.of("config", "nextcloud-mcp.yaml"),
            Path.of("config", "nextcloud-mcp.yml"),
            Path.of("nextcloud-mcp.yaml"),
            Path.of("nextcloud-mcp.yml"));

    private ConfigPaths() {
    }

    public static Optional<Path> findFromSystemSources(String explicitPath) {
        Optional<Path> explicit = regularFile(explicitPath);
        if (explicit.isPresent()) {
            return explicit;
        }
        Optional<Path> property = regularFile(System.getProperty("nextcloud.mcp.config"));
        if (property.isPresent()) {
            return property;
        }
        Optional<Path> environment = regularFile(System.getenv("NEXTCLOUD_MCP_CONFIG"));
        if (environment.isPresent()) {
            return environment;
        }
        return findDefault();
    }

    public static Optional<Path> findDefault() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            for (Path candidate : DEFAULT_CANDIDATES) {
                Path path = current.resolve(candidate).normalize();
                if (Files.isRegularFile(path)) {
                    return Optional.of(path);
                }
            }
            current = current.getParent();
        }
        return Optional.empty();
    }

    private static Optional<Path> regularFile(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        Path path = Path.of(value.trim());
        return Files.isRegularFile(path) ? Optional.of(path) : Optional.empty();
    }
}
