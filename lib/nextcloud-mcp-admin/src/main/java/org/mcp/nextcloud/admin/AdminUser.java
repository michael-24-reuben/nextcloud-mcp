package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;

public record AdminUser(
        String id,
        String displayName,
        String email,
        boolean enabled,
        String quota,
        JsonNode raw) {
}
