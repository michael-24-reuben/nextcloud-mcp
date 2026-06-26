package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminIdentity(String id, String displayName, String email, boolean enabled, JsonNode raw) {
}
