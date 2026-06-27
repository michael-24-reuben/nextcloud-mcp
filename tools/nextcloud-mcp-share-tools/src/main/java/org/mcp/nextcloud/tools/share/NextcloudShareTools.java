package org.mcp.nextcloud.tools.share;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.ShareCreateRequest;
import org.mcp.nextcloud.client.ShareInfo;
import org.mcp.nextcloud.client.SharePermission;
import org.mcp.nextcloud.client.ShareType;
import org.mcp.nextcloud.client.ShareUpdateRequest;
import org.mcp.nextcloud.client.Sharee;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.result.ErrorResult;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolInputSchema;
import org.mcp.nextcloud.tool.api.ToolInvocation;
import org.mcp.nextcloud.tool.api.ToolOutputSchema;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.api.ToolSecurity;
import org.mcp.nextcloud.tool.api.ToolValueType;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;

public final class NextcloudShareTools {
    private static final String SHARES_READ = "nextcloud.shares.read";
    private static final String SHARES_WRITE = "nextcloud.shares.write";

    private NextcloudShareTools() {
    }

    public static List<ToolRegistration> registrations(NextcloudClient client) {
        NextcloudClient nextcloud = Preconditions.requireNonNull(client, "nextcloud client");
        return List.of(
                tool("nextcloud.shares.list", "List shares visible to the authenticated user.", false, Set.of(SHARES_READ),
                        params(optionalString("path", "Optional file path filter.")),
                        invocation -> shares(nextcloud.shares().listShares(optionalStringArg(invocation, "path"))), false),
                tool("nextcloud.shares.get", "Get one share by id.", false, Set.of(SHARES_READ),
                        params(requiredString("shareId", "Share id to read.")),
                        invocation -> share(nextcloud.shares().getShare(stringArg(invocation, "shareId"))), false),
                tool("nextcloud.shares.create", "Create a share.", false, Set.of(SHARES_WRITE),
                        params(requiredString("path", "Path to share."),
                                optionalString("shareType", "Share type: public_link, user, group, email, remote, or circle."),
                                optionalString("shareWith", "User, group, email, remote, or circle target."),
                                optionalInteger("permissions", "Bitmask permissions; defaults to read."),
                                optionalBoolean("publicUpload", "Allow public upload for folder shares."),
                                optionalString("password", "Optional share password."),
                                optionalString("expireDate", "Optional expiration date."),
                                optionalString("note", "Optional recipient note."),
                                optionalString("label", "Optional share label.")),
                        invocation -> share(nextcloud.shares().createShare(createRequest(invocation))), false),
                tool("nextcloud.shares.update", "Update an existing share.", false, Set.of(SHARES_WRITE),
                        params(requiredString("shareId", "Share id to update."),
                                optionalInteger("permissions", "Optional replacement permission bit mask."),
                                optionalString("password", "Optional replacement password."),
                                optionalBoolean("publicUpload", "Optional public-upload flag."),
                                optionalString("expireDate", "Optional expiration date."),
                                optionalString("note", "Optional share note."),
                                optionalString("label", "Optional share label."),
                                optionalString("attributes", "Optional raw share attributes JSON."),
                                optionalBoolean("sendMail", "Whether Nextcloud should send an update email.")),
                        invocation -> share(nextcloud.shares().updateShare(
                                stringArg(invocation, "shareId"),
                                updateRequest(invocation))), false),
                tool("nextcloud.shares.delete", "Delete a share by id.", true, Set.of(SHARES_WRITE),
                        params(requiredString("shareId", "Share id to delete.")),
                        invocation -> {
                            String shareId = stringArg(invocation, "shareId");
                            nextcloud.shares().deleteShare(shareId);
                            return ToolResult.ok(Map.of("shareId", shareId, "deleted", true));
                        }, false),
                tool("nextcloud.shares.send_email", "Send a share notification email.", false, Set.of(SHARES_WRITE),
                        params(requiredString("shareId", "Share id to notify.")),
                        invocation -> {
                            String shareId = stringArg(invocation, "shareId");
                            nextcloud.shares().sendShareEmail(shareId);
                            return ToolResult.ok(Map.of("shareId", shareId, "emailSent", true));
                        }, false),
                tool("nextcloud.sharees.search", "Search share recipients.", false, Set.of(SHARES_READ),
                        params(requiredString("query", "Sharee search query."),
                                optionalString("itemType", "Nextcloud item type; defaults to file."),
                                optionalInteger("page", "Optional result page."),
                                optionalInteger("perPage", "Optional page size.")),
                        invocation -> sharees(nextcloud.sharees().search(
                                stringArg(invocation, "query"),
                                optionalStringArg(invocation, "itemType"),
                                intArg(invocation, "page", 0),
                                intArg(invocation, "perPage", 0))), false),
                tool("nextcloud.sharees.recommended", "List recommended share recipients.", false, Set.of(SHARES_READ),
                        params(optionalString("itemType", "Nextcloud item type; defaults to file.")),
                        invocation -> sharees(nextcloud.sharees().recommended(optionalStringArg(invocation, "itemType"))), false));
    }

    private static ToolRegistration tool(
            String name,
            String description,
            boolean destructive,
            Set<String> scopes,
            ToolInputSchema inputSchema,
            ToolHandlerFunction handler,
            boolean deferred) {
        ToolDescriptor descriptor = new ToolDescriptor(
                new ToolId(name),
                name,
                description,
                inputSchema,
                ToolOutputSchema.object(),
                new ToolSecurity(scopes, destructive),
                Map.of("category", "shares", "deferred", deferred));
        return new ToolRegistration(descriptor, handler::invoke);
    }

    private static ToolRegistration deferred(
            String name,
            String description,
            boolean destructive,
            Set<String> scopes,
            ToolInputSchema inputSchema,
            String reason) {
        return tool(name, description, destructive, scopes, inputSchema,
                invocation -> ToolResult.failed(new ErrorResult("tool.deferred", reason, Map.of("tool", name))), true);
    }

    private static ToolResult shares(List<ShareInfo> shares) {
        return ToolResult.ok(Map.of("shares", shares.stream().map(NextcloudShareTools::shareMap).toList()));
    }

    private static ToolResult share(ShareInfo share) {
        return ToolResult.ok(shareMap(share));
    }

    private static ToolResult sharees(List<Sharee> sharees) {
        return ToolResult.ok(Map.of("sharees", sharees.stream().map(NextcloudShareTools::shareeMap).toList()));
    }

    private static ShareCreateRequest createRequest(ToolInvocation invocation) {
        return new ShareCreateRequest(
                stringArg(invocation, "path"),
                shareType(optionalStringArg(invocation, "shareType")),
                optionalStringArg(invocation, "shareWith"),
                intArg(invocation, "permissions", SharePermission.READ),
                optionalBooleanArg(invocation, "publicUpload"),
                optionalStringArg(invocation, "password"),
                optionalStringArg(invocation, "expireDate"),
                optionalStringArg(invocation, "note"),
                optionalStringArg(invocation, "label"));
    }

    private static ShareUpdateRequest updateRequest(ToolInvocation invocation) {
        return new ShareUpdateRequest(
                optionalIntegerArg(invocation, "permissions"),
                optionalStringArg(invocation, "password"),
                optionalBooleanArg(invocation, "publicUpload"),
                optionalStringArg(invocation, "expireDate"),
                optionalStringArg(invocation, "note"),
                optionalStringArg(invocation, "label"),
                optionalStringArg(invocation, "attributes"),
                optionalBooleanArg(invocation, "sendMail"));
    }

    private static ShareType shareType(String value) {
        if (value == null || value.isBlank() || "public_link".equalsIgnoreCase(value)) {
            return ShareType.PUBLIC_LINK;
        }
        return ShareType.valueOf(value.trim().replace('-', '_').toUpperCase());
    }

    private static Map<String, Object> shareMap(ShareInfo share) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "id", share.id());
        put(values, "path", share.path());
        put(values, "shareType", share.shareType());
        put(values, "shareWith", share.shareWith());
        put(values, "shareWithDisplayName", share.shareWithDisplayName());
        put(values, "url", share.url());
        put(values, "permissions", share.permissions());
        put(values, "expiration", share.expiration());
        put(values, "token", share.token());
        return Map.copyOf(values);
    }

    private static Map<String, Object> shareeMap(Sharee sharee) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "kind", sharee.kind());
        put(values, "label", sharee.label());
        put(values, "value", sharee.value());
        put(values, "shareType", sharee.shareType());
        return Map.copyOf(values);
    }

    private static ToolInputSchema params(ToolParameter... parameters) {
        return new ToolInputSchema(List.of(parameters), false);
    }

    private static ToolParameter requiredString(String name, String description) {
        return ToolParameter.required(name, ToolValueType.STRING, description);
    }

    private static ToolParameter optionalString(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.STRING, description);
    }

    private static ToolParameter optionalInteger(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.INTEGER, description);
    }

    private static ToolParameter optionalBoolean(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.BOOLEAN, description);
    }

    private static String stringArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value instanceof String text && !text.isBlank()) {
            return text;
        }
        throw new IllegalArgumentException(name + " must be a non-blank string");
    }

    private static String optionalStringArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return null;
        }
        if (value instanceof String text) {
            return text.isBlank() ? null : text;
        }
        throw new IllegalArgumentException(name + " must be a string");
    }

    private static int intArg(ToolInvocation invocation, String name, int defaultValue) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalArgumentException(name + " must be an integer");
    }

    private static Integer optionalIntegerArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalArgumentException(name + " must be an integer");
    }

    private static Boolean optionalBooleanArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return null;
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
