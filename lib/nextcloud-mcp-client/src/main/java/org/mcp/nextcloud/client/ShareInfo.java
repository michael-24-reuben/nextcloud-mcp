package org.mcp.nextcloud.client;

import com.fasterxml.jackson.databind.JsonNode;

public record ShareInfo(
        String id,
        String path,
        Integer shareType,
        String shareWith,
        String shareWithDisplayName,
        String url,
        Integer permissions,
        String expiration,
        String token,
        JsonNode raw) {
}
