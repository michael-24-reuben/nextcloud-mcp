package org.mcp.nextcloud.admin;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudAdminAppsClient extends AbstractNextcloudAdminClient {
    private static final String APPS_ENDPOINT = "/ocs/v1.php/cloud/apps";

    private final AdminOcsParser ocsParser;

    NextcloudAdminAppsClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, AdminOcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public List<String> listApps() {
        return listApps(null);
    }

    public List<String> listEnabledApps() {
        return listApps("enabled");
    }

    public List<String> listDisabledApps() {
        return listApps("disabled");
    }

    public List<String> listApps(String filter) {
        Map<String, String> query = filter == null || filter.isBlank()
                ? Map.of()
                : Map.of("filter", filter);
        HttpResponseSpec response = sendExpecting(getOcs(APPS_ENDPOINT, query), 200);
        JsonNode data = ocsParser.data(response, APPS_ENDPOINT);
        return AdminOcsParser.stringList(data, "apps");
    }

    public AdminApp getAppInfo(String appId) {
        String endpoint = appEndpoint(appId);
        HttpResponseSpec response = sendExpecting(getOcs(endpoint), 200);
        return parseApp(ocsParser.data(response, endpoint), appId);
    }

    public AdminAppOperation enableApp(String appId) {
        String endpoint = appEndpoint(appId);
        HttpResponseSpec response = sendExpecting(requests.postOcs(endpoint), 200);
        ocsParser.data(response, endpoint);
        return new AdminAppOperation(endpoint, response.statusCode(), AdminRiskLevel.CRITICAL);
    }

    public AdminAppOperation disableApp(String appId) {
        String endpoint = appEndpoint(appId);
        HttpResponseSpec response = sendExpecting(deleteOcs(endpoint), 200);
        ocsParser.data(response, endpoint);
        return new AdminAppOperation(endpoint, response.statusCode(), AdminRiskLevel.CRITICAL);
    }

    private static AdminApp parseApp(JsonNode data, String fallbackId) {
        String id = AdminOcsParser.text(data, "id", "appid", "appId");
        String name = AdminOcsParser.text(data, "name");
        String version = AdminOcsParser.text(data, "version");
        Boolean enabled = data.has("enabled") && !data.path("enabled").isNull()
                ? data.path("enabled").asBoolean()
                : null;
        return new AdminApp(id == null ? fallbackId : id, name, version, enabled, data);
    }

    private static String appEndpoint(String appId) {
        return APPS_ENDPOINT + "/" + NextcloudHttpRequestFactory.encodePathSegment(Preconditions.requireNonBlank(appId, "app id"));
    }
}
