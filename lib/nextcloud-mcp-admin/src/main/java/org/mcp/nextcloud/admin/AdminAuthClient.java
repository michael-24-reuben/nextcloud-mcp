package org.mcp.nextcloud.admin;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class AdminAuthClient extends AbstractNextcloudAdminClient {
    private static final String CURRENT_USER_ENDPOINT = "/ocs/v1.php/cloud/user";

    private final AdminOcsParser ocsParser;

    AdminAuthClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, AdminOcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public AdminIdentity testAdminIdentity() {
        HttpResponseSpec response = sendExpecting(getOcs(CURRENT_USER_ENDPOINT), 200);
        JsonNode data = ocsParser.data(response, CURRENT_USER_ENDPOINT);
        String id = AdminOcsParser.text(data, "id", "uid");
        String displayName = AdminOcsParser.text(data, "display-name", "displayname", "displayName");
        String email = AdminOcsParser.text(data, "email");
        boolean enabled = !data.has("enabled") || data.path("enabled").asBoolean(true);
        return new AdminIdentity(id, displayName, email, enabled, data);
    }
}
