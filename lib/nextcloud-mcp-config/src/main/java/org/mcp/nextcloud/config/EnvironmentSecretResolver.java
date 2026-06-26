package org.mcp.nextcloud.config;

import java.util.Optional;

public final class EnvironmentSecretResolver implements SecretResolver {
    @Override
    public Optional<String> resolve(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(System.getenv(name));
    }
}
