package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminApp(String id, String name, String version, Boolean enabled, JsonNode raw) {
}
