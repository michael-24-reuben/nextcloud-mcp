package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminGroup(String id, String displayName, JsonNode raw) {
}
