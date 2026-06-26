package org.mcp.nextcloud.tools.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mcp.nextcloud.client.NextcloudCapabilities;
import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudUser;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolInputSchema;
import org.mcp.nextcloud.tool.api.ToolOutputSchema;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.api.ToolSecurity;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;

public final class NextcloudUserTools {
    private static final String USER_READ = "nextcloud.user.read";

    private NextcloudUserTools() {
    }

    public static List<ToolRegistration> registrations(NextcloudClient client) {
        NextcloudClient nextcloud = Preconditions.requireNonNull(client, "nextcloud client");
        return List.of(
                tool("nextcloud.user.me", "Read the authenticated Nextcloud user.", invocation ->
                        user(nextcloud.users().currentUser())),
                tool("nextcloud.user.capabilities", "Read server capabilities visible to the authenticated user.", invocation ->
                        capabilities(nextcloud.users().capabilities())),
                tool("nextcloud.user.metadata", "Read self metadata for the authenticated account.", invocation ->
                        userMetadata(nextcloud.users().currentUser())));
    }

    private static ToolRegistration tool(String name, String description, ToolHandlerFunction handler) {
        ToolDescriptor descriptor = new ToolDescriptor(
                new ToolId(name),
                name,
                description,
                ToolInputSchema.empty(),
                ToolOutputSchema.object(),
                new ToolSecurity(Set.of(USER_READ), false),
                Map.of("category", "user"));
        return new ToolRegistration(descriptor, handler::invoke);
    }

    private static ToolResult user(NextcloudUser user) {
        return ToolResult.ok(userMap(user));
    }

    private static ToolResult userMetadata(NextcloudUser user) {
        Map<String, Object> values = new LinkedHashMap<>(userMap(user));
        values.put("metadataScope", "authenticated-account");
        if (user.raw() != null && !user.raw().isMissingNode()) {
            values.put("raw", user.raw());
        }
        return ToolResult.ok(Map.copyOf(values));
    }

    private static ToolResult capabilities(NextcloudCapabilities capabilities) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "versionString", capabilities.versionString());
        if (capabilities.capabilities() != null && !capabilities.capabilities().isMissingNode()) {
            values.put("capabilities", capabilities.capabilities());
        }
        if (capabilities.raw() != null && !capabilities.raw().isMissingNode()) {
            values.put("raw", capabilities.raw());
        }
        return ToolResult.ok(Map.copyOf(values));
    }

    private static Map<String, Object> userMap(NextcloudUser user) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "id", user.id());
        put(values, "displayName", user.displayName());
        put(values, "email", user.email());
        values.put("enabled", user.enabled());
        return Map.copyOf(values);
    }

    private static void put(Map<String, Object> values, String name, Object value) {
        if (value != null) {
            values.put(name, value);
        }
    }

    @FunctionalInterface
    private interface ToolHandlerFunction {
        ToolResult invoke(org.mcp.nextcloud.tool.api.ToolInvocation invocation) throws Exception;
    }
}
