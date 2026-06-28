package org.mcp.nextcloud.tools.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.admin.NextcloudAdminClient;
import org.mcp.nextcloud.admin.NextcloudAdminCredentials;
import org.mcp.nextcloud.admin.NextcloudAdminOccBridge;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.core.id.PrincipalId;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.security.Principal;
import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.security.Scope;
import org.mcp.nextcloud.security.Scopes;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.runtime.InMemoryToolRegistry;
import org.mcp.nextcloud.tool.runtime.ToolDispatcher;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;
import org.mcp.nextcloud.tool.runtime.ToolRuntimeContext;

class NextcloudAdminToolsTest {
    @Test
    void exposesAdminDescriptorsWithRiskMetadata() {
        List<ToolRegistration> registrations = NextcloudAdminTools.registrations(client(new RecordingHttpClient()));
        List<String> names = registrations.stream().map(registration -> registration.descriptor().name()).toList();

        assertTrue(names.contains("nextcloud.admin.users.list"));
        assertTrue(names.contains("nextcloud.admin.users.delete"));
        assertTrue(names.contains("nextcloud.admin.apps.enable"));
        assertTrue(names.contains("nextcloud.admin.occ.maintenance_mode"));

        ToolRegistration deleteUser = registration(registrations, "nextcloud.admin.users.delete");
        assertTrue(deleteUser.descriptor().security().destructive());
        assertEquals("critical", deleteUser.descriptor().metadata().get("risk"));
        assertEquals(true, deleteUser.descriptor().metadata().get("confirmationRequired"));
        assertTrue(deleteUser.descriptor().security().requiredScopes().contains(NextcloudAdminTools.ADMIN_DELETE));

        ToolRegistration listUsers = registration(registrations, "nextcloud.admin.users.list");
        assertFalse(listUsers.descriptor().security().destructive());
        assertEquals("low", listUsers.descriptor().metadata().get("risk"));
    }

    @Test
    void policyDeniesAdminToolBeforeHandlerInvocation() {
        RecordingHttpClient http = new RecordingHttpClient();
        InMemoryToolRegistry registry = registry(NextcloudAdminTools.registrations(client(http)));
        ToolDispatcher dispatcher = new ToolDispatcher(registry);

        ToolResult result = dispatcher.invoke(
                new ToolId("nextcloud.admin.users.delete"),
                Map.of("userId", "temporary"),
                runtimeContext(false, Set.of(new Scope(NextcloudAdminTools.ADMIN_DELETE), Scopes.Files.DELETE)));

        assertFalse(result.success());
        assertEquals("tool.policy_denied", result.error().code());
        assertEquals(0, http.requests().size());
    }

    @Test
    void allowedAdminReadToolInvokesClient() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"users":["admin","temporary"]}}}
                """));
        InMemoryToolRegistry registry = registry(NextcloudAdminTools.registrations(client(http)));
        ToolDispatcher dispatcher = new ToolDispatcher(registry);

        ToolResult result = dispatcher.invoke(
                new ToolId("nextcloud.admin.users.list"),
                Map.of("limit", 10),
                runtimeContext(true, Set.of(new Scope(NextcloudAdminTools.ADMIN_READ))));

        assertTrue(result.success());
        Map<?, ?> structured = (Map<?, ?>) result.structuredContent();
        assertEquals(List.of("admin", "temporary"), structured.get("users"));
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users?limit=10", http.requests().getFirst().uri().toString());
    }

    @Test
    void occToolReturnsCriticalCommandPlanOnly() {
        InMemoryToolRegistry registry = registry(NextcloudAdminTools.registrations(
                client(new RecordingHttpClient()),
                new NextcloudAdminOccBridge("nextcloud-aio-nextcloud")));
        ToolDispatcher dispatcher = new ToolDispatcher(registry);

        ToolResult result = dispatcher.invoke(
                new ToolId("nextcloud.admin.occ.files_scan"),
                Map.of("userId", "temporary"),
                runtimeContext(true, Set.of(new Scope(NextcloudAdminTools.ADMIN_OCC), Scopes.Files.DELETE)));

        assertTrue(result.success());
        Map<?, ?> structured = (Map<?, ?>) result.structuredContent();
        assertEquals("files_scan", structured.get("operation"));
        assertEquals("critical", structured.get("risk"));
        assertEquals(false, structured.get("executes"));
        assertEquals(List.of("docker", "exec", "-u", "www-data", "nextcloud-aio-nextcloud", "php", "occ", "files:scan", "temporary"),
                structured.get("command"));
    }

    private static InMemoryToolRegistry registry(List<ToolRegistration> registrations) {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        registrations.forEach(registry::register);
        return registry;
    }

    private static ToolRegistration registration(List<ToolRegistration> registrations, String name) {
        return registrations.stream()
                .filter(registration -> registration.descriptor().name().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private static ToolRuntimeContext runtimeContext(boolean admin, Set<Scope> scopes) {
        Principal principal = new Principal(new PrincipalId("tester"), "Tester", admin, scopes);
        PrincipalContext principalContext = new PrincipalContext(principal, new AccountId("admin"), new InvocationId("inv-1"));
        return new ToolRuntimeContext(principalContext, Map.of("source", "unit-test"));
    }

    private static NextcloudAdminClient client(RecordingHttpClient http) {
        return new NextcloudAdminClient(http, NextcloudAdminCredentials.of(
                "admin", "https://cloud.example.com/", "admin", "app-password"));
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
