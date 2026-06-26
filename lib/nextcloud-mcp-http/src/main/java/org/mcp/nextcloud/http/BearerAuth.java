package org.mcp.nextcloud.http;

import org.mcp.nextcloud.core.util.Preconditions;

public final class BearerAuth {
    private BearerAuth() {
    }

    public static String authorizationHeader(String token) {
        return "Bearer " + Preconditions.requireNonBlank(token, "token");
    }
}
