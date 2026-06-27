package org.mcp.nextcloud.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudShareesClient extends AbstractNextcloudClient {
    private static final String SHAREES_ENDPOINT = "/ocs/v2.php/apps/files_sharing/api/v1/sharees";
    private static final String RECOMMENDED_ENDPOINT = "/ocs/v2.php/apps/files_sharing/api/v1/sharees_recommended";

    private final OcsParser ocsParser;

    NextcloudShareesClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, OcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public List<Sharee> search(String query, String itemType, int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("search", Preconditions.requireNonBlank(query, "sharee query"));
        params.put("itemType", itemType == null || itemType.isBlank() ? "file" : itemType);
        if (page > 0) {
            params.put("page", Integer.toString(page));
        }
        if (perPage > 0) {
            params.put("perPage", Integer.toString(perPage));
        }
        HttpResponseSpec response = sendExpecting(requests.getOcs(SHAREES_ENDPOINT, params), 200);
        return shareesFrom(ocsParser.data(response, SHAREES_ENDPOINT));
    }

    public List<Sharee> recommended(String itemType) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("itemType", itemType == null || itemType.isBlank() ? "file" : itemType);
        HttpResponseSpec response = sendExpecting(requests.getOcs(RECOMMENDED_ENDPOINT, params), 200);
        return shareesFrom(ocsParser.data(response, RECOMMENDED_ENDPOINT));
    }

    private static List<Sharee> shareesFrom(JsonNode data) {
        List<Sharee> sharees = new ArrayList<>();
        collectSection(sharees, data.path("exact"));
        collectSection(sharees, data);
        return List.copyOf(sharees);
    }

    private static void collectSection(List<Sharee> sharees, JsonNode section) {
        collectKind(sharees, "users", section.path("users"));
        collectKind(sharees, "groups", section.path("groups"));
        collectKind(sharees, "remotes", section.path("remotes"));
        collectKind(sharees, "emails", section.path("emails"));
        collectKind(sharees, "circles", section.path("circles"));
    }

    private static void collectKind(List<Sharee> sharees, String kind, JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        nodes.forEach(node -> sharees.add(new Sharee(
                kind,
                OcsParser.text(node, "label", "displayName", "displayname"),
                OcsParser.text(node, "value", "name", "id"),
                intValue(node, "shareType", "share_type"),
                node)));
    }

    private static Integer intValue(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.path(name);
            if (!value.isMissingNode() && !value.isNull() && value.canConvertToInt()) {
                return value.asInt();
            }
        }
        return null;
    }
}
