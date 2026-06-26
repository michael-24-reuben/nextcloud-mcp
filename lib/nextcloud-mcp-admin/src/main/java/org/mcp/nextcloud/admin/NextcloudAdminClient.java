package org.mcp.nextcloud.admin;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudAdminClient {
    private final AdminAuthClient auth;
    private final NextcloudAdminUsersClient users;
    private final NextcloudAdminGroupsClient groups;
    private final NextcloudAdminAppsClient apps;
    private final NextcloudAdminSharesSupport shares;

    public NextcloudAdminClient(HttpClientAdapter httpClient, NextcloudAdminCredentials credentials) {
        HttpClientAdapter adapter = Preconditions.requireNonNull(httpClient, "http client");
        NextcloudAdminCredentials adminCredentials = Preconditions.requireNonNull(credentials, "credentials");
        NextcloudHttpRequestFactory requests = new NextcloudHttpRequestFactory(
                adminCredentials.baseUri(),
                adminCredentials.username(),
                adminCredentials.appPassword());
        AdminOcsParser ocsParser = new AdminOcsParser();
        this.auth = new AdminAuthClient(adapter, requests, ocsParser);
        this.users = new NextcloudAdminUsersClient(adapter, requests, ocsParser);
        this.groups = new NextcloudAdminGroupsClient(adapter, requests, ocsParser);
        this.apps = new NextcloudAdminAppsClient(adapter, requests, ocsParser);
        this.shares = new NextcloudAdminSharesSupport(adapter, adminCredentials);
    }

    public AdminAuthClient auth() {
        return auth;
    }

    public NextcloudAdminUsersClient users() {
        return users;
    }

    public NextcloudAdminGroupsClient groups() {
        return groups;
    }

    public NextcloudAdminAppsClient apps() {
        return apps;
    }

    public NextcloudAdminSharesSupport shares() {
        return shares;
    }
}
