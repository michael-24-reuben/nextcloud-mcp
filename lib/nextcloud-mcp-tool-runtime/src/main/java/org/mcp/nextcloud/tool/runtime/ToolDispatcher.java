package org.mcp.nextcloud.tool.runtime;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.result.ErrorResult;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.security.AuditEvent;
import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolInvocation;
import org.mcp.nextcloud.tool.api.ToolInvocationContext;
import org.mcp.nextcloud.tool.api.ToolResult;

public final class ToolDispatcher {
    private final ToolRegistry registry;
    private final ToolArgumentValidator validator;
    private final ToolPolicyInterceptor policyInterceptor;
    private final ToolAuditSink auditSink;

    public ToolDispatcher(ToolRegistry registry) {
        this(registry, new ToolArgumentValidator(), new DefaultToolPolicyInterceptor(), ToolAuditSink.noop());
    }

    public ToolDispatcher(
            ToolRegistry registry,
            ToolArgumentValidator validator,
            ToolPolicyInterceptor policyInterceptor,
            ToolAuditSink auditSink) {
        this.registry = Preconditions.requireNonNull(registry, "tool registry");
        this.validator = Preconditions.requireNonNull(validator, "tool argument validator");
        this.policyInterceptor = Preconditions.requireNonNull(policyInterceptor, "tool policy interceptor");
        this.auditSink = auditSink == null ? ToolAuditSink.noop() : auditSink;
    }

    public List<ToolDescriptor> listTools() {
        return registry.list().stream()
                .map(ToolRegistration::descriptor)
                .toList();
    }

    public ToolResult invoke(ToolId toolId, Map<String, Object> arguments, ToolRuntimeContext runtimeContext) {
        runtimeContext = Preconditions.requireNonNull(runtimeContext, "runtime context");
        PrincipalContext principalContext = runtimeContext.principalContext();
        ToolRegistration registration = registry.find(toolId).orElse(null);
        if (registration == null) {
            ToolResult result = ToolResult.failed(new ErrorResult("tool.not_found", "Tool not found: " + toolId.value(), Map.of()));
            audit(principalContext, toolId, "not_found", result.error().details());
            return result;
        }

        ToolDescriptor descriptor = registration.descriptor();
        ToolValidationResult validation = validator.validate(descriptor, arguments);
        if (!validation.valid()) {
            Map<String, Object> details = Map.of("errors", validation.errors());
            ToolResult result = ToolResult.failed(new ErrorResult("tool.validation_failed", "Tool arguments failed validation", details));
            audit(principalContext, toolId, "validation_failed", details);
            return result;
        }

        ToolPolicyDecision decision = policyInterceptor.evaluate(descriptor, principalContext);
        if (!decision.allowed()) {
            Map<String, Object> details = Map.of("reason", decision.reason());
            ToolResult result = ToolResult.failed(new ErrorResult("tool.policy_denied", decision.reason(), details));
            audit(principalContext, toolId, "policy_denied", details);
            return result;
        }

        ToolInvocationContext invocationContext = new ToolInvocationContext(
                principalContext.invocationId(),
                principalContext.accountId(),
                principalContext.principal().id().value(),
                runtimeContext.attributes());
        ToolInvocation invocation = new ToolInvocation(toolId, invocationContext, arguments);
        try {
            ToolResult result = registration.handler().invoke(invocation);
            ToolResult normalized = result == null
                    ? ToolResult.failed(new ErrorResult("tool.empty_result", "Tool returned no result", Map.of()))
                    : result;
            audit(principalContext, toolId, normalized.success() ? "success" : "failed", normalized.metadata());
            return normalized;
        } catch (Exception ex) {
            Map<String, Object> details = Map.of("exception", ex.getClass().getName());
            ToolResult result = ToolResult.failed(new ErrorResult("tool.execution_failed", ex.getMessage() == null ? "Tool execution failed" : ex.getMessage(), details));
            audit(principalContext, toolId, "execution_failed", details);
            return result;
        }
    }

    private void audit(PrincipalContext context, ToolId toolId, String outcome, Map<String, Object> attributes) {
        Map<String, Object> eventAttributes = new LinkedHashMap<>();
        if (attributes != null) {
            eventAttributes.putAll(attributes);
        }
        auditSink.record(new AuditEvent(
                Instant.now(),
                context.invocationId(),
                toolId,
                context.accountId(),
                context.principal() == null ? "" : context.principal().id().value(),
                outcome,
                eventAttributes));
    }
}
