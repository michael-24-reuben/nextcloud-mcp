package org.mcp.nextcloud.tools.files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

class NextcloudFilesToolsTest {
    @Test
    void exposesExpectedFileToolDescriptors() {
        List<ToolRegistration> registrations = NextcloudFilesTools.registrations(client(new RecordingHttpClient()));

        assertEquals(List.of(
                "nextcloud.files.list",
                "nextcloud.files.stat",
                "nextcloud.files.download",
                "nextcloud.files.upload",
                "nextcloud.files.mkdir",
                "nextcloud.files.delete",
                "nextcloud.files.move",
                "nextcloud.files.copy",
                "nextcloud.files.search",
                "nextcloud.files.favorite"), names(registrations));
        ToolRegistration delete = registration(registrations, "nextcloud.files.delete");
        assertTrue(delete.descriptor().security().destructive());
        assertTrue(delete.descriptor().security().requiredScopes().contains("nextcloud.files.delete"));
    }

    @Test
    void listUsesInvocationAccountIdForWebDavPath() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(xml("""
                <?xml version="1.0" encoding="utf-8"?>
                <d:multistatus xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Codex%20Scratch/</d:href>
                    <d:propstat><d:prop><d:resourcetype><d:collection/></d:resourcetype></d:prop></d:propstat>
                  </d:response>
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Codex%20Scratch/note.txt</d:href>
                    <d:propstat><d:prop>
                      <d:getcontentlength>5</d:getcontentlength>
                      <d:getcontenttype>text/plain</d:getcontenttype>
                      <oc:owner-id>temporary</oc:owner-id>
                    </d:prop></d:propstat>
                  </d:response>
                </d:multistatus>
                """));
        ToolRegistration list = registration(NextcloudFilesTools.registrations(client(http)), "nextcloud.files.list");

        ToolResult result = list.handler().invoke(invocation(list, Map.of("path", "/Codex Scratch")));

        assertTrue(result.success());
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/Codex%20Scratch/", http.requests().getFirst().uri().toString());
        assertEquals(HttpMethod.PROPFIND, http.requests().getFirst().method());
        Map<?, ?> structured = (Map<?, ?>) result.structuredContent();
        List<?> resources = (List<?>) structured.get("resources");
        assertEquals(1, resources.size());
    }

    @Test
    void uploadAcceptsPlainTextContent() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(empty(201));
        ToolRegistration upload = registration(NextcloudFilesTools.registrations(client(http)), "nextcloud.files.upload");

        ToolResult result = upload.handler().invoke(invocation(upload, Map.of(
                "path", "/Codex Scratch/file.txt",
                "content", "hello")));

        assertTrue(result.success());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.PUT, request.method());
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/Codex%20Scratch/file.txt", request.uri().toString());
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), request.body());
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

    private static HttpResponseSpec xml(String body) {
        return new HttpResponseSpec(207, Map.of("Content-Type", List.of("application/xml")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec empty(int statusCode) {
        return new HttpResponseSpec(statusCode, Map.of(), new byte[0]);
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
