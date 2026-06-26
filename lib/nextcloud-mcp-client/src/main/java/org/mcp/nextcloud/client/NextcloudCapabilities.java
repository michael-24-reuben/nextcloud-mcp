package org.mcp.nextcloud.client;

import com.fasterxml.jackson.databind.JsonNode;

public record NextcloudCapabilities(String versionString, JsonNode capabilities, JsonNode raw) {
}
