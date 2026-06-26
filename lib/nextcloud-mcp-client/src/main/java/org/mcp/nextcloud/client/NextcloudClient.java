package org.mcp.nextcloud.client;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;

public final class NextcloudClient {
    private final NextcloudUsersClient users;
    private final NextcloudFilesClient files;
    private final NextcloudSharesClient shares;
    private final NextcloudShareesClient sharees;

    public NextcloudClient(HttpClientAdapter httpClient, NextcloudCredentials credentials) {
        HttpClientAdapter adapter = Preconditions.requireNonNull(httpClient, "http client");
        NextcloudRequestFactory requests = new NextcloudRequestFactory(
                Preconditions.requireNonNull(credentials, "credentials"));
        OcsParser ocsParser = new OcsParser();
        WebDavParser webDavParser = new WebDavParser();
        this.users = new NextcloudUsersClient(adapter, requests, ocsParser);
        this.files = new NextcloudFilesClient(adapter, requests, webDavParser);
        this.shares = new NextcloudSharesClient(adapter, requests, ocsParser);
        this.sharees = new NextcloudShareesClient(adapter, requests, ocsParser);
    }

    public NextcloudUsersClient users() {
        return users;
    }

    public NextcloudFilesClient files() {
        return files;
    }

    public NextcloudSharesClient shares() {
        return shares;
    }

    public NextcloudShareesClient sharees() {
        return sharees;
    }
}
