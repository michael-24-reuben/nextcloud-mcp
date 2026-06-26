package org.mcp.nextcloud.client;

import org.mcp.nextcloud.http.HttpMethod;

public record WebDavOperation(HttpMethod method, String path, int statusCode) {
}
