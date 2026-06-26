package org.mcp.nextcloud.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.mcp.nextcloud.http.HttpResponseSpec;

final class OcsParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    JsonNode data(HttpResponseSpec response, String endpoint) {
        JsonNode root = readJson(response, endpoint);
        JsonNode ocs = root.path("ocs");
        if (ocs.isMissingNode()) {
            throw new NextcloudClientException("invalid_ocs_response", "Missing OCS envelope for " + endpoint);
        }
        JsonNode meta = ocs.path("meta");
        String status = text(meta, "status");
        int statusCode = meta.path("statuscode").asInt(response.statusCode());
        if (!"ok".equalsIgnoreCase(status) || statusCode != 100) {
            String message = text(meta, "message");
            throw new NextcloudApiException(
                    "nextcloud_ocs_error",
                    message == null || message.isBlank() ? "OCS request failed for " + endpoint : message,
                    response.statusCode());
        }
        return ocs.path("data");
    }

    private JsonNode readJson(HttpResponseSpec response, String endpoint) {
        try {
            return objectMapper.readTree(new String(response.body(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new NextcloudClientException("invalid_ocs_json", "Invalid OCS JSON for " + endpoint, ex);
        }
    }

    static String text(JsonNode node, String... names) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        for (String name : names) {
            JsonNode value = node.path(name);
            if (!value.isMissingNode() && !value.isNull()) {
                String text = value.asText();
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }
        }
        return null;
    }
}
