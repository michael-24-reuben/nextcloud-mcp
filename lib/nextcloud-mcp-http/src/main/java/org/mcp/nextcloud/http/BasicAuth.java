package org.mcp.nextcloud.http;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.mcp.nextcloud.core.util.Preconditions;

public final class BasicAuth {
    private BasicAuth() {
    }

    public static String authorizationHeader(String username, String password) {
        String user = Preconditions.requireNonBlank(username, "username");
        String secret = Preconditions.requireNonBlank(password, "password");
        String token = Base64.getEncoder().encodeToString((user + ":" + secret).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }
}
