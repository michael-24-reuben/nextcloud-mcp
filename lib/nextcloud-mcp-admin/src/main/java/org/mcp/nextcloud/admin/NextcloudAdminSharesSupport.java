package org.mcp.nextcloud.admin;

import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.client.NextcloudSharesClient;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;

public final class NextcloudAdminSharesSupport {
    private final NextcloudClient client;

    NextcloudAdminSharesSupport(HttpClientAdapter httpClient, NextcloudAdminCredentials credentials) {
        NextcloudAdminCredentials adminCredentials = Preconditions.requireNonNull(credentials, "credentials");
        NextcloudCredentials shareCredentials = new NextcloudCredentials(
                adminCredentials.accountId(),
                adminCredentials.baseUri(),
                adminCredentials.username(),
                adminCredentials.appPassword());
        this.client = new NextcloudClient(Preconditions.requireNonNull(httpClient, "http client"), shareCredentials);
    }

    public NextcloudSharesClient shares() {
        return client.shares();
    }
}
