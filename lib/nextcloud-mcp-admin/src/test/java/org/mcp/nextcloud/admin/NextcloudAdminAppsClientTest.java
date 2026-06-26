package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudAdminAppsClientTest {
    @Test
    void listAppsSupportsInstalledEnabledAndDisabledFilters() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(apps("files", "dav"));
        http.enqueue(apps("files"));
        http.enqueue(apps("circles"));
        NextcloudAdminClient client = client(http);

        assertEquals(List.of("files", "dav"), client.apps().listApps());
        assertEquals(List.of("files"), client.apps().listEnabledApps());
        assertEquals(List.of("circles"), client.apps().listDisabledApps());

        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/apps", http.requests().get(0).uri().toString());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/apps?filter=enabled", http.requests().get(1).uri().toString());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/apps?filter=disabled", http.requests().get(2).uri().toString());
    }

    @Test
    void appInfoAndMutationsUseProvisioningAppRoutesWithCriticalRisk() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"files_sharing",
                  "name":"Files sharing",
                  "version":"1.0.0",
                  "enabled":true
                }}}
                """));
        http.enqueue(ok());
        http.enqueue(ok());
        NextcloudAdminClient client = client(http);

        AdminApp app = client.apps().getAppInfo("files sharing");
        AdminAppOperation enabled = client.apps().enableApp("files sharing");
        AdminAppOperation disabled = client.apps().disableApp("files sharing");

        assertEquals("files_sharing", app.id());
        assertEquals("Files sharing", app.name());
        assertEquals("1.0.0", app.version());
        assertEquals(Boolean.TRUE, app.enabled());
        assertEquals(AdminRiskLevel.CRITICAL, enabled.riskLevel());
        assertEquals(AdminRiskLevel.CRITICAL, disabled.riskLevel());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/apps/files%20sharing", http.requests().get(0).uri().toString());
        assertEquals(HttpMethod.GET, http.requests().get(0).method());
        assertEquals(HttpMethod.POST, http.requests().get(1).method());
        assertEquals(HttpMethod.DELETE, http.requests().get(2).method());
    }

    private static NextcloudAdminClient client(RecordingHttpClient http) {
        return new NextcloudAdminClient(http, NextcloudAdminCredentials.of(
                "admin", "https://cloud.example.com/", "admin", "app-password"));
    }

    private static HttpResponseSpec apps(String... ids) {
        String values = String.join(",", java.util.Arrays.stream(ids).map(id -> "\"" + id + "\"").toList());
        return json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"apps":[%s]}}}
                """.formatted(values));
    }

    private static HttpResponseSpec ok() {
        return json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":[]}}
                """);
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
