package org.mcp.nextcloud.client;

import com.fasterxml.jackson.databind.JsonNode;

public record Sharee(String kind, String label, String value, Integer shareType, JsonNode raw) {
}
