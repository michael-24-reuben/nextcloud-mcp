package org.mcp.nextcloud.server;

import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.http.HttpClientAdapter;

@FunctionalInterface
public interface NextcloudHttpClientFactory {
    HttpClientAdapter create(NextcloudCredentials credentials);
}
