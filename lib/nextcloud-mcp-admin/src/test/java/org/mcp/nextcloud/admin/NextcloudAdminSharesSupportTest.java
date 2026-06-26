package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.client.ShareInfo;
import org.mcp.nextcloud.http.BasicAuth;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudAdminSharesSupportTest {
    @Test
    void adminShareSupportUsesNormalShareApiWithAdminCredentials() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":[{
                  "id":"42",
                  "path":"/AdminScratch/file.txt",
                  "share_type":3,
                  "permissions":1,
                  "url":"https://cloud.example.com/s/abc"
                }]}}
                """));
        NextcloudAdminClient client = new NextcloudAdminClient(http, NextcloudAdminCredentials.of(
                "admin", "https://cloud.example.com/", "admin", "app-password"));

        List<ShareInfo> shares = client.shares().shares().listShares("/AdminScratch/file.txt");

        assertEquals(1, shares.size());
        assertEquals("42", shares.getFirst().id());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.GET, request.method());
        assertEquals("https://cloud.example.com/ocs/v2.php/apps/files_sharing/api/v1/shares?path=%2FAdminScratch%2Ffile.txt",
                request.uri().toString());
        assertEquals(BasicAuth.authorizationHeader("admin", "app-password"), request.headers().get("Authorization").getFirst());
        assertEquals("true", request.headers().get("OCS-APIRequest").getFirst());
    }

    private static HttpResponseSpec json(String body) {
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static final class RecordingHttpClient implements HttpClientAdapter {
        private final ArrayDeque<HttpResponseSpec> responses = new ArrayDeque<>();
        private final List<HttpRequestSpec> requests = new ArrayList<>();

        @Override
        public HttpResponseSpec send(HttpRequestSpec request) {
            requests.add(request);
            return responses.removeFirst();
        }

        void enqueue(HttpResponseSpec response) {
            responses.addLast(response);
        }

        List<HttpRequestSpec> requests() {
            return requests;
        }
    }
}
