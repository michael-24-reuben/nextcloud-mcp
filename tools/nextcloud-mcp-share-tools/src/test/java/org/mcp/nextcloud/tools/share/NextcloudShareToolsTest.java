package org.mcp.nextcloud.tools.share;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

class NextcloudShareToolsTest {
    @Test
    void exposesSupportedAndDeferredShareDescriptors() throws Exception {
        List<ToolRegistration> registrations = NextcloudShareTools.registrations(client(new RecordingHttpClient()));

        assertEquals(List.of(
                "nextcloud.shares.list",
                "nextcloud.shares.get",
                "nextcloud.shares.create",
                "nextcloud.shares.update",
                "nextcloud.shares.delete",
                "nextcloud.shares.send_email",
                "nextcloud.sharees.search",
                "nextcloud.sharees.recommended"), names(registrations));
        ToolRegistration delete = registration(registrations, "nextcloud.shares.delete");
        assertTrue(delete.descriptor().security().destructive());
        ToolRegistration get = registration(registrations, "nextcloud.shares.get");
        assertEquals(true, get.descriptor().metadata().get("deferred"));

        ToolResult result = get.handler().invoke(invocation(get, Map.of("shareId", "42")));

        assertFalse(result.success());
        assertEquals("tool.deferred", result.error().code());
    }

    @Test
    void createShareUsesOcsV2FormRequest() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"42",
                  "path":"/CodexScratch/file.txt",
                  "share_type":3,
                  "url":"https://cloud.example.com/s/abc",
                  "permissions":1,
                  "token":"abc"
                }}}
                """));
        ToolRegistration create = registration(NextcloudShareTools.registrations(client(http)), "nextcloud.shares.create");

        ToolResult result = create.handler().invoke(invocation(create, Map.of("path", "/CodexScratch/file.txt")));

        assertTrue(result.success());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.POST, request.method());
        assertEquals("https://cloud.example.com/ocs/v2.php/apps/files_sharing/api/v1/shares", request.uri().toString());
        assertEquals("path=%2FCodexScratch%2Ffile.txt&shareType=3&permissions=1", body(request));
    }

    @Test
    void searchShareesCallsShareeEndpoint() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "exact":{"users":[{"label":"Michael","value":"temporary","shareType":0}]},
                  "users":[{"label":"Other","value":"other","shareType":0}]
                }}}
                """));
        ToolRegistration search = registration(NextcloudShareTools.registrations(client(http)), "nextcloud.sharees.search");

        ToolResult result = search.handler().invoke(invocation(search, Map.of(
                "query", "temp",
                "page", 1,
                "perPage", 25)));

        assertTrue(result.success());
        String uri = http.requests().getFirst().uri().toString();
        assertTrue(uri.contains("/ocs/v2.php/apps/files_sharing/api/v1/sharees?"));
        assertTrue(uri.contains("search=temp"));
        assertTrue(uri.contains("perPage=25"));
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

    private static ToolInvocation invocation(ToolRegistration registration, Map<String, Object> arguments) {
        return new ToolInvocation(registration.descriptor().id(), context(), arguments);
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

    private static String body(HttpRequestSpec request) {
        return new String(request.body(), StandardCharsets.UTF_8);
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
