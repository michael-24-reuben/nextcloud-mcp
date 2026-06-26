package org.mcp.nextcloud.config;

public record ServerConfig(boolean enabled, String host, int port) {
    public static ServerConfig defaults() {
        return new ServerConfig(true, "127.0.0.1", 8080);
    }
}
