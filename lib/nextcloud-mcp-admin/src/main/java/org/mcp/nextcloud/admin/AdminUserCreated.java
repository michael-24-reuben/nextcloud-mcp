package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminUserCreated(String id, JsonNode raw) {
}
