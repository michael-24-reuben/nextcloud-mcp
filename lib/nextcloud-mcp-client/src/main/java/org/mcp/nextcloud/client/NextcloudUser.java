package org.mcp.nextcloud.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.core.util.Preconditions;

public record NextcloudUser(String id, String displayName, String email, boolean enabled, JsonNode raw) {
    public NextcloudUser {
        id = Preconditions.requireNonBlank(id, "user id");
    }
}
