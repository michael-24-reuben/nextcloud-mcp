package org.mcp.nextcloud.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudUsersClient extends AbstractNextcloudClient {
    private static final String CURRENT_USER_ENDPOINT = "/ocs/v1.php/cloud/user";
    private static final String CAPABILITIES_ENDPOINT = "/ocs/v1.php/cloud/capabilities";

    private final OcsParser ocsParser;

    NextcloudUsersClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, OcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public NextcloudUser currentUser() {
        HttpResponseSpec response = sendExpecting(requests.getOcs(CURRENT_USER_ENDPOINT), 200);
        JsonNode data = ocsParser.data(response, CURRENT_USER_ENDPOINT);
        String id = OcsParser.text(data, "id", "uid");
        String displayName = OcsParser.text(data, "display-name", "displayname", "displayName");
        String email = OcsParser.text(data, "email");
        boolean enabled = !data.has("enabled") || data.path("enabled").asBoolean(true);
        return new NextcloudUser(id, displayName, email, enabled, data);
    }

    public NextcloudCapabilities capabilities() {
        HttpResponseSpec response = sendExpecting(requests.getOcs(CAPABILITIES_ENDPOINT), 200);
        JsonNode data = ocsParser.data(response, CAPABILITIES_ENDPOINT);
        JsonNode version = data.path("version");
        String versionString = OcsParser.text(version, "string");
        return new NextcloudCapabilities(versionString, data.path("capabilities"), data);
    }
}
