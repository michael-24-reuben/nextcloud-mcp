package org.mcp.nextcloud.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudSharesClient extends AbstractNextcloudClient {
    private static final String SHARES_ENDPOINT = "/ocs/v2.php/apps/files_sharing/api/v1/shares";

    private final OcsParser ocsParser;

    NextcloudSharesClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, OcsParser ocsParser) {
        super(httpClient, requests);
        this.ocsParser = ocsParser;
    }

    public List<ShareInfo> listShares() {
        return listShares(null);
    }

    public List<ShareInfo> listShares(String path) {
        Map<String, String> query = path == null || path.isBlank() ? Map.of() : Map.of("path", path);
        HttpResponseSpec response = sendExpecting(requests.getOcs(SHARES_ENDPOINT, query), 200);
        return sharesFrom(ocsParser.data(response, SHARES_ENDPOINT));
    }

    public ShareInfo getShare(String shareId) {
        String endpoint = shareEndpoint(shareId);
        HttpResponseSpec response = sendExpecting(requests.getOcs(endpoint), 200);
        return shareFrom(ocsParser.data(response, endpoint));
    }

    public ShareInfo createShare(ShareCreateRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("path", request.path());
        fields.put("shareType", Integer.toString(request.shareType().code()));
        fields.put("shareWith", request.shareWith());
        fields.put("permissions", Integer.toString(request.permissions()));
        if (request.publicUpload() != null) {
            fields.put("publicUpload", request.publicUpload() ? "true" : "false");
        }
        fields.put("password", request.password());
        fields.put("expireDate", request.expireDate());
        fields.put("note", request.note());
        fields.put("label", request.label());

        HttpResponseSpec response = sendExpecting(requests.postOcsForm(SHARES_ENDPOINT, fields), 200, 201);
        return shareFrom(ocsParser.data(response, SHARES_ENDPOINT));
    }

    public ShareInfo updateShare(String shareId, ShareUpdateRequest request) {
        String endpoint = shareEndpoint(shareId);
        Map<String, String> fields = new LinkedHashMap<>();
        if (request.permissions() != null) {
            fields.put("permissions", Integer.toString(request.permissions()));
        }
        fields.put("password", request.password());
        if (request.publicUpload() != null) {
            fields.put("publicUpload", request.publicUpload() ? "true" : "false");
        }
        fields.put("expireDate", request.expireDate());
        fields.put("note", request.note());
        fields.put("label", request.label());
        fields.put("attributes", request.attributes());
        if (request.sendMail() != null) {
            fields.put("sendMail", request.sendMail() ? "true" : "false");
        }
        HttpResponseSpec response = sendExpecting(requests.putOcsForm(endpoint, fields), 200);
        return shareFrom(ocsParser.data(response, endpoint));
    }

    public void deleteShare(String shareId) {
        sendExpecting(requests.deleteOcs(shareEndpoint(shareId)), 200, 204);
    }

    public void sendShareEmail(String shareId) {
        sendExpecting(requests.postOcs(shareEndpoint(shareId) + "/send-email"), 200);
    }

    private static String shareEndpoint(String shareId) {
        return SHARES_ENDPOINT + "/" + NextcloudHttpRequestFactory.encodePathSegment(shareId);
    }

    private static List<ShareInfo> sharesFrom(JsonNode data) {
        if (data == null || data.isMissingNode() || data.isNull()) {
            return List.of();
        }
        List<ShareInfo> shares = new ArrayList<>();
        if (data.isArray()) {
            data.forEach(node -> shares.add(shareFrom(node)));
        } else {
            shares.add(shareFrom(data));
        }
        return List.copyOf(shares);
    }

    private static ShareInfo shareFrom(JsonNode node) {
        return new ShareInfo(
                OcsParser.text(node, "id"),
                OcsParser.text(node, "path"),
                intValue(node, "share_type", "shareType"),
                OcsParser.text(node, "share_with", "shareWith"),
                OcsParser.text(node, "share_with_displayname", "shareWithDisplayName"),
                OcsParser.text(node, "url"),
                intValue(node, "permissions"),
                OcsParser.text(node, "expiration"),
                OcsParser.text(node, "token"),
                node);
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
