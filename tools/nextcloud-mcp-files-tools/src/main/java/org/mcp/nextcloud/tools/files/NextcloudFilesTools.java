package org.mcp.nextcloud.tools.files;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.WebDavOperation;
import org.mcp.nextcloud.client.WebDavResource;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.security.Scope;
import org.mcp.nextcloud.security.Scopes;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolInputSchema;
import org.mcp.nextcloud.tool.api.ToolInvocation;
import org.mcp.nextcloud.tool.api.ToolOutputSchema;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.api.ToolSecurity;
import org.mcp.nextcloud.tool.api.ToolValueType;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;

public final class NextcloudFilesTools {
    private NextcloudFilesTools() {
    }

    public static List<ToolRegistration> registrations(NextcloudClient client) {
        NextcloudClient nextcloud = Preconditions.requireNonNull(client, "nextcloud client");
        return List.of(
                tool(Scopes.Files.LIST,
                        params(requiredString("path", "Folder path to list.")),
                        invocation -> resources(nextcloud.files().list(userId(invocation), stringArg(invocation, "path")))),
                tool(Scopes.Files.STAT,
                        params(requiredString("path", "File or folder path to inspect.")),
                        invocation -> resource(nextcloud.files().stat(userId(invocation), stringArg(invocation, "path")))),
                tool(Scopes.Files.DOWNLOAD,
                        params(requiredString("path", "File path to download.")),
                        invocation -> download(nextcloud, invocation)),
                tool(Scopes.Files.UPLOAD,
                        params(requiredString("path", "Destination file path."),
                                requiredString("content", "Text content or base64 bytes."),
                                optionalBoolean("base64", "Set true when content is base64 encoded.")),
                        invocation -> operation(nextcloud.files().upload(userId(invocation), stringArg(invocation, "path"), uploadBytes(invocation)))),
                tool(Scopes.Files.MKDIR,
                        params(requiredString("path", "Folder path to create.")),
                        invocation -> operation(nextcloud.files().mkdir(userId(invocation), stringArg(invocation, "path")))),
                tool(Scopes.Files.DELETE,
                        params(requiredString("path", "File or folder path to delete.")),
                        invocation -> operation(nextcloud.files().delete(userId(invocation), stringArg(invocation, "path")))),
                tool(Scopes.Files.MOVE,
                        params(requiredString("fromPath", "Source file or folder path."),
                                requiredString("toPath", "Destination file or folder path.")),
                        invocation -> operation(nextcloud.files().move(userId(invocation), stringArg(invocation, "fromPath"), stringArg(invocation, "toPath")))),
                tool(Scopes.Files.COPY,
                        params(requiredString("fromPath", "Source file or folder path."),
                                requiredString("toPath", "Destination file or folder path.")),
                        invocation -> operation(nextcloud.files().copy(userId(invocation), stringArg(invocation, "fromPath"), stringArg(invocation, "toPath")))),
                tool(Scopes.Files.SEARCH,
                        params(requiredString("query", "Search query.")),
                        invocation -> resources(nextcloud.files().search(userId(invocation), stringArg(invocation, "query")))),
                tool(Scopes.Files.FAVORITE,
                        params(requiredString("path", "File or folder path."),
                                requiredBoolean("favorite", "Desired favorite flag.")),
                        invocation -> operation(nextcloud.files().favorite(userId(invocation), stringArg(invocation, "path"), booleanArg(invocation, "favorite")))));
    }

    private static ToolRegistration tool(Scope scope, ToolInputSchema inputSchema, ToolHandlerFunction handler) {
        ToolSecurity security = new ToolSecurity(scope.prerequisites().stream().map(Scope::value)
                .collect(Collectors.toSet()), scope.destructive());

        ToolDescriptor descriptor = new ToolDescriptor(
                new ToolId(scope.value()),
                scope.value(),
                scope.description(),
                inputSchema,
                ToolOutputSchema.object(),
                security,
                Map.of("category", "files"));
        return new ToolRegistration(descriptor, handler::invoke);
    }

    private static ToolResult resources(List<WebDavResource> resources) {
        return ToolResult.ok(Map.of("resources", resources.stream().map(NextcloudFilesTools::resourceMap).toList()));
    }

    private static ToolResult resource(WebDavResource resource) {
        return ToolResult.ok(resourceMap(resource));
    }

    private static ToolResult operation(WebDavOperation operation) {
        return ToolResult.ok(Map.of(
                "method", operation.method().name(),
                "path", operation.path(),
                "statusCode", operation.statusCode()));
    }

    private static ToolResult download(NextcloudClient nextcloud, ToolInvocation invocation) {
        String path = stringArg(invocation, "path");
        byte[] bytes = nextcloud.files().download(userId(invocation), path);
        return ToolResult.ok(Map.of(
                "path", path,
                "byteLength", bytes.length,
                "contentBase64", Base64.getEncoder().encodeToString(bytes)));
    }

    private static Map<String, Object> resourceMap(WebDavResource resource) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "href", resource.href());
        put(values, "name", resource.name());
        values.put("collection", resource.collection());
        put(values, "size", resource.size());
        put(values, "contentType", resource.contentType());
        put(values, "etag", resource.etag());
        put(values, "lastModified", resource.lastModified());
        put(values, "favorite", resource.favorite());
        put(values, "permissions", resource.permissions());
        put(values, "ownerId", resource.ownerId());
        put(values, "ownerDisplayName", resource.ownerDisplayName());
        return Map.copyOf(values);
    }

    private static byte[] uploadBytes(ToolInvocation invocation) {
        String content = stringArg(invocation, "content");
        if (booleanArg(invocation, "base64", false)) {
            return Base64.getDecoder().decode(content);
        }
        return content.getBytes(StandardCharsets.UTF_8);
    }

    private static ToolInputSchema params(ToolParameter... parameters) {
        return new ToolInputSchema(List.of(parameters), false);
    }

    private static ToolParameter requiredString(String name, String description) {
        return ToolParameter.required(name, ToolValueType.STRING, description);
    }

    private static ToolParameter requiredBoolean(String name, String description) {
        return ToolParameter.required(name, ToolValueType.BOOLEAN, description);
    }

    private static ToolParameter optionalBoolean(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.BOOLEAN, description);
    }

    private static String userId(ToolInvocation invocation) {
        return invocation.context().accountId().value();
    }

    private static String stringArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value instanceof String text && !text.isBlank()) {
            return text;
        }
        throw new IllegalArgumentException(name + " must be a non-blank string");
    }

    private static boolean booleanArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalArgumentException(name + " must be a boolean");
    }

    private static boolean booleanArg(ToolInvocation invocation, String name, boolean defaultValue) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalArgumentException(name + " must be a boolean");
    }

    private static void put(Map<String, Object> values, String name, Object value) {
        if (value != null) {
            values.put(name, value);
        }
    }

    @FunctionalInterface
    private interface ToolHandlerFunction {
        ToolResult invoke(ToolInvocation invocation) throws Exception;
    }
}
