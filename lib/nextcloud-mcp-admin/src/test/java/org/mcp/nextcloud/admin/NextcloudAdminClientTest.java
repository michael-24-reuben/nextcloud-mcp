package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.config.NextcloudAccountConfig;
import org.mcp.nextcloud.config.NextcloudAdminConfig;
import org.mcp.nextcloud.config.NextcloudMcpConfig;
import org.mcp.nextcloud.config.ServerConfig;
import org.mcp.nextcloud.config.ToolCatalogConfig;
import org.mcp.nextcloud.core.error.ConfigurationException;
import org.mcp.nextcloud.http.BasicAuth;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudAdminClientTest {
    @Test
    void adminIdentityUsesConfiguredAdminAccountAndOcsHeaders() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"admin",
                  "display-name":"Admin User",
                  "email":"admin@example.com",
                  "enabled":true
                }}}
                """));
        NextcloudAdminCredentials credentials = NextcloudAdminCredentials.fromConfig(config(), name -> "NC_ADMIN_APP_PASSWORD".equals(name)
                ? java.util.Optional.of("admin-app-password")
                : java.util.Optional.empty());
        NextcloudAdminClient client = new NextcloudAdminClient(http, credentials);

        AdminIdentity identity = client.auth().testAdminIdentity();

        assertEquals("admin", identity.id());
        assertEquals("Admin User", identity.displayName());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.GET, request.method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/user", request.uri().toString());
        assertHeader(request, "OCS-APIRequest", "true");
        assertHeader(request, "Accept", "application/json");
        assertHeader(request, "Authorization", BasicAuth.authorizationHeader("admin", "admin-app-password"));
    }

    @Test
    void adminCredentialsRequireEnabledAdminConfig() {
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(),
                Map.of(),
                new ToolCatalogConfig(Map.of()),
                NextcloudAdminConfig.disabled());

        ConfigurationException error = assertThrows(
                ConfigurationException.class,
                () -> NextcloudAdminCredentials.fromConfig(config, name -> java.util.Optional.empty()));

        assertEquals("admin_disabled", error.code());
    }

    @Test
    void adminCredentialsRequireAccountMarkedAdmin() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "main", "https://cloud.example.com", "temporary", "app-password", true, false, true, List.of());
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(),
                Map.of("main", account),
                new ToolCatalogConfig(Map.of()),
                new NextcloudAdminConfig(true, "main"));

        ConfigurationException error = assertThrows(
                ConfigurationException.class,
                () -> NextcloudAdminCredentials.fromConfig(config, name -> java.util.Optional.empty()));

        assertEquals("admin_account_not_marked_admin", error.code());
    }

    private static NextcloudMcpConfig config() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "admin",
                "https://cloud.example.com/",
                "admin",
                "${NC_ADMIN_APP_PASSWORD}",
                false,
                true,
                true,
                List.of("nextcloud.admin"));
        return new NextcloudMcpConfig(
                ServerConfig.defaults(),
                Map.of("admin", account),
                new ToolCatalogConfig(Map.of()),
                new NextcloudAdminConfig(true, "admin"));
    }

    private static HttpResponseSpec json(String body) {
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static void assertHeader(HttpRequestSpec request, String name, String value) {
        assertEquals(value, request.headers().get(name).getFirst());
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
