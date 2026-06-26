package org.mcp.nextcloud.tools.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.tool.api.ToolInvocation;
import org.mcp.nextcloud.tool.api.ToolInvocationContext;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;

class NextcloudUserToolsTest {
    @Test
    void exposesAuthenticatedUserDescriptors() {
        List<ToolRegistration> registrations = NextcloudUserTools.registrations(client(new RecordingHttpClient()));

        assertEquals(List.of(
                "nextcloud.user.me",
                "nextcloud.user.capabilities",
                "nextcloud.user.metadata"), names(registrations));
        assertTrue(registrations.stream()
                .allMatch(registration -> registration.descriptor().security().requiredScopes().contains("nextcloud.user.read")));
    }

    @Test
    void currentUserToolReadsSelfOnlyEndpoint() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"temporary",
                  "display-name":"tempo",
                  "email":null,
                  "enabled":true
                }}}
                """));
        ToolRegistration me = registration(NextcloudUserTools.registrations(client(http)), "nextcloud.user.me");

        ToolResult result = me.handler().invoke(invocation(me));

        assertTrue(result.success());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.GET, request.method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/user", request.uri().toString());
        Map<?, ?> structured = (Map<?, ?>) result.structuredContent();
        assertEquals("temporary", structured.get("id"));
        assertEquals("tempo", structured.get("displayName"));
    }

    @Test
    void capabilitiesToolReadsCapabilitiesEndpoint() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "version":{"string":"34.0.0"},
                  "capabilities":{"files_sharing":{"api_enabled":true}}
                }}}
                """));
        ToolRegistration capabilities = registration(NextcloudUserTools.registrations(client(http)), "nextcloud.user.capabilities");

        ToolResult result = capabilities.handler().invoke(invocation(capabilities));

        assertTrue(result.success());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/capabilities", http.requests().getFirst().uri().toString());
        Map<?, ?> structured = (Map<?, ?>) result.structuredContent();
        assertEquals("34.0.0", structured.get("versionString"));
    }

    private static List<String> names(List<ToolRegistration> registrations) {
        return registrations.stream().map(registration -> registration.descriptor().name()).toList();
    }

    private static ToolRegistration registration(List<ToolRegistration> registrations, String name) {
        return registrations.stream()
                .filter(registration -> registration.descriptor().name().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private static ToolInvocation invocation(ToolRegistration registration) {
        return new ToolInvocation(registration.descriptor().id(), context(), Map.of());
    }

    private static ToolInvocationContext context() {
        return new ToolInvocationContext(new InvocationId("inv-1"), new AccountId("temporary"), "tester", Map.of());
    }

    private static NextcloudClient client(RecordingHttpClient http) {
        return new NextcloudClient(http, NextcloudCredentials.of(
                "main", "https://cloud.example.com/", "temporary", "app-password"));
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
