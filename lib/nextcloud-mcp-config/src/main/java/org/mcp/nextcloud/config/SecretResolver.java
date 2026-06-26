package org.mcp.nextcloud.config;

import java.util.Optional;

public interface SecretResolver {
    Optional<String> resolve(String name);
}
