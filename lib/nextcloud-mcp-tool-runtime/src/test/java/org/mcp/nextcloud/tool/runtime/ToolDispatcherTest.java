package org.mcp.nextcloud.tool.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.core.id.PrincipalId;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.security.*;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolInputSchema;
import org.mcp.nextcloud.tool.api.ToolOutputSchema;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.api.ToolSecurity;
import org.mcp.nextcloud.tool.api.ToolValueType;

class ToolDispatcherTest {
    @Test
    void listsAndInvokesRegisteredTool() {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        ToolId echoId = new ToolId("nextcloud.test.echo");
        registry.register(new ToolRegistration(echoDescriptor(echoId, false), invocation -> ToolResult.ok(Map.of(
                "message", invocation.arguments().get("message"),
                "principal", invocation.context().principalId()))));
        List<AuditEvent> auditEvents = new ArrayList<>();
        ToolDispatcher dispatcher = new ToolDispatcher(registry, new ToolArgumentValidator(), new DefaultToolPolicyInterceptor(), auditEvents::add);

        ToolResult result = dispatcher.invoke(echoId, Map.of("message", "hello"), runtimeContext(Set.of(Scopes.Files.READ)));

        assertTrue(result.success());
        assertEquals(List.of(echoDescriptor(echoId, false)), dispatcher.listTools());
        assertInstanceOf(Map.class, result.structuredContent());
        assertEquals("success", auditEvents.getFirst().outcome());
    }

    @Test
    void rejectsInvalidArgumentsBeforeHandlerRuns() {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        ToolId echoId = new ToolId("nextcloud.test.echo");
        registry.register(new ToolRegistration(echoDescriptor(echoId, false), invocation -> {
            throw new AssertionError("handler should not run");
        }));
        ToolDispatcher dispatcher = new ToolDispatcher(registry);

        ToolResult result = dispatcher.invoke(echoId, Map.of("extra", true), runtimeContext(Set.of(Scopes.Files.READ)));

        assertFalse(result.success());
        assertEquals("tool.validation_failed", result.error().code());
    }

    @Test
    void deniesInvocationWhenPolicyRejectsRequiredScope() {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        ToolId deleteId = new ToolId("nextcloud.test.delete");
        registry.register(new ToolRegistration(echoDescriptor(deleteId, true), invocation -> ToolResult.ok(Map.of())));
        List<AuditEvent> auditEvents = new ArrayList<>();
        ToolDispatcher dispatcher = new ToolDispatcher(registry, new ToolArgumentValidator(), new DefaultToolPolicyInterceptor(), auditEvents::add);

        ToolResult result = dispatcher.invoke(deleteId, Map.of("message", "delete"), runtimeContext(Set.of(Scopes.Files.READ)));

        assertFalse(result.success());
        assertEquals("tool.policy_denied", result.error().code());
        assertEquals("policy_denied", auditEvents.getFirst().outcome());
    }

    @Test
    void duplicateToolIdsAreRejected() {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        ToolId toolId = new ToolId("nextcloud.test.echo");
        registry.register(new ToolRegistration(echoDescriptor(toolId, false), invocation -> ToolResult.ok(Map.of())));

        assertThrows(IllegalArgumentException.class, () ->
                registry.register(new ToolRegistration(echoDescriptor(toolId, false), invocation -> ToolResult.ok(Map.of()))));
    }

    @Test
    void mapperConvertsArgumentsToTypedRequest() {
        ToolArgumentMapper mapper = new ToolArgumentMapper();

        EchoRequest request = mapper.convert(Map.of("message", "hello", "count", 2), EchoRequest.class);

        assertEquals("hello", request.message());
        assertEquals(2, request.count());
    }

    private ToolDescriptor echoDescriptor(ToolId toolId, boolean destructive) {
        return new ToolDescriptor(
                toolId,
                toolId.value(),
                "Test echo tool",
                new ToolInputSchema(List.of(
                        ToolParameter.required("message", ToolValueType.STRING, "Message to echo")), false),
                ToolOutputSchema.object(),
                new ToolSecurity(destructive ? Set.of(Scopes.Files.DELETE.value()) : Set.of(Scopes.Files.READ.value()), destructive),
                Map.of());
    }

    private ToolRuntimeContext runtimeContext(Set<Scope> scopes) {
        Principal principal = new Principal(new PrincipalId("tester"), "Tester", false, scopes);
        PrincipalContext principalContext = new PrincipalContext(principal, new AccountId("temporary"), new InvocationId("inv-1"));
        return new ToolRuntimeContext(principalContext, Map.of("source", "unit-test"));
    }

    private record EchoRequest(String message, int count) {
    }
}
