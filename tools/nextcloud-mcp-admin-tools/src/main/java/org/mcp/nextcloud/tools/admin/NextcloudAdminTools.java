package org.mcp.nextcloud.tools.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mcp.nextcloud.admin.AdminApp;
import org.mcp.nextcloud.admin.AdminAppOperation;
import org.mcp.nextcloud.admin.AdminGroup;
import org.mcp.nextcloud.admin.AdminOccCommandPlan;
import org.mcp.nextcloud.admin.AdminProvisioningOperation;
import org.mcp.nextcloud.admin.AdminRiskLevel;
import org.mcp.nextcloud.admin.AdminUser;
import org.mcp.nextcloud.admin.AdminUserCreateRequest;
import org.mcp.nextcloud.admin.NextcloudAdminClient;
import org.mcp.nextcloud.admin.NextcloudAdminOccBridge;
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

public final class NextcloudAdminTools {
    public static final String ADMIN_READ = "nextcloud.admin.read";
    public static final String ADMIN_WRITE = "nextcloud.admin.write";
    public static final String ADMIN_DELETE = "nextcloud.admin.delete";
    public static final String ADMIN_APPS = "nextcloud.admin.apps";
    public static final String ADMIN_OCC = "nextcloud.admin.occ";

    private NextcloudAdminTools() {
    }

    public static List<ToolRegistration> registrations(NextcloudAdminClient client) {
        return registrations(client, new NextcloudAdminOccBridge());
    }

    public static List<ToolRegistration> registrations(NextcloudAdminClient client, NextcloudAdminOccBridge occBridge) {
        NextcloudAdminClient admin = Preconditions.requireNonNull(client, "admin client");
        NextcloudAdminOccBridge occ = Preconditions.requireNonNull(occBridge, "occ bridge");
        return List.of(
                tool("nextcloud.admin.users.list", "List users as an admin.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(optionalString("search", "Optional user search text."),
                                optionalInteger("limit", "Optional result limit."),
                                optionalInteger("offset", "Optional result offset.")),
                        invocation -> ToolResult.ok(Map.of("users", admin.users().listUsers(
                                optionalStringArg(invocation, "search"),
                                optionalIntegerArg(invocation, "limit"),
                                optionalIntegerArg(invocation, "offset"))))),
                tool("nextcloud.admin.users.get", "Read one user as an admin.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(requiredString("userId", "User id to read.")),
                        invocation -> ToolResult.ok(userMap(admin.users().getUser(stringArg(invocation, "userId"))))),
                tool("nextcloud.admin.users.create", "Create a user.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to create."),
                                requiredString("password", "Initial app/user password value."),
                                optionalString("displayName", "Optional display name."),
                                optionalString("email", "Optional email."),
                                optionalString("quota", "Optional quota."),
                                optionalArray("groups", "Optional group ids."),
                                optionalArray("subadmins", "Optional subadmin group ids.")),
                        invocation -> ToolResult.ok(Map.of("userId", admin.users().createUser(createUserRequest(invocation)).id()))),
                tool("nextcloud.admin.users.update_field", "Update an editable user field.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to update."),
                                requiredString("key", "Editable field key."),
                                requiredString("value", "New field value.")),
                        invocation -> operation(admin.users().updateUserField(
                                stringArg(invocation, "userId"),
                                stringArg(invocation, "key"),
                                stringArg(invocation, "value")))),
                tool("nextcloud.admin.users.disable", "Disable a user.", AdminRiskLevel.HIGH, true, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to disable.")),
                        invocation -> operation(admin.users().disableUser(stringArg(invocation, "userId")))),
                tool("nextcloud.admin.users.enable", "Enable a user.", AdminRiskLevel.HIGH, true, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to enable.")),
                        invocation -> operation(admin.users().enableUser(stringArg(invocation, "userId")))),
                tool("nextcloud.admin.users.delete", "Delete a user.", AdminRiskLevel.CRITICAL, true, Set.of(ADMIN_DELETE),
                        params(requiredString("userId", "User id to delete.")),
                        invocation -> operation(admin.users().deleteUser(stringArg(invocation, "userId")))),
                tool("nextcloud.admin.users.groups", "List groups for a user.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(requiredString("userId", "User id to inspect.")),
                        invocation -> ToolResult.ok(Map.of("groups", admin.users().getUserGroups(stringArg(invocation, "userId"))))),
                tool("nextcloud.admin.users.subadmins", "List subadmin groups for a user.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(requiredString("userId", "User id to inspect.")),
                        invocation -> ToolResult.ok(Map.of("subadmins", admin.users().getSubadminGroups(stringArg(invocation, "userId"))))),
                tool("nextcloud.admin.groups.list", "List groups as an admin.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(optionalString("search", "Optional group search text."),
                                optionalInteger("limit", "Optional result limit."),
                                optionalInteger("offset", "Optional result offset.")),
                        invocation -> ToolResult.ok(Map.of("groups", admin.groups().listGroups(
                                optionalStringArg(invocation, "search"),
                                optionalIntegerArg(invocation, "limit"),
                                optionalIntegerArg(invocation, "offset"))))),
                tool("nextcloud.admin.groups.create", "Create a group.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("groupId", "Group id to create.")),
                        invocation -> ToolResult.ok(groupMap(admin.groups().createGroup(stringArg(invocation, "groupId"))))),
                tool("nextcloud.admin.groups.members", "List group members.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(requiredString("groupId", "Group id to inspect.")),
                        invocation -> ToolResult.ok(Map.of("users", admin.groups().getGroupMembers(stringArg(invocation, "groupId"))))),
                tool("nextcloud.admin.groups.subadmins", "List group subadmins.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ),
                        params(requiredString("groupId", "Group id to inspect.")),
                        invocation -> ToolResult.ok(Map.of("subadmins", admin.groups().getGroupSubadmins(stringArg(invocation, "groupId"))))),
                tool("nextcloud.admin.groups.update_display_name", "Update a group display name.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("groupId", "Group id to update."),
                                requiredString("displayName", "New display name.")),
                        invocation -> operation(admin.groups().updateGroupDisplayName(
                                stringArg(invocation, "groupId"),
                                stringArg(invocation, "displayName")))),
                tool("nextcloud.admin.groups.delete", "Delete a group.", AdminRiskLevel.HIGH, true, Set.of(ADMIN_DELETE),
                        params(requiredString("groupId", "Group id to delete.")),
                        invocation -> operation(admin.groups().deleteGroup(stringArg(invocation, "groupId")))),
                tool("nextcloud.admin.groups.add_user", "Add a user to a group.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to add."),
                                requiredString("groupId", "Group id.")),
                        invocation -> operation(admin.groups().addUserToGroup(stringArg(invocation, "userId"), stringArg(invocation, "groupId")))),
                tool("nextcloud.admin.groups.remove_user", "Remove a user from a group.", AdminRiskLevel.MEDIUM, false, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to remove."),
                                requiredString("groupId", "Group id.")),
                        invocation -> operation(admin.groups().removeUserFromGroup(stringArg(invocation, "userId"), stringArg(invocation, "groupId")))),
                tool("nextcloud.admin.subadmins.promote", "Promote a user to subadmin for a group.", AdminRiskLevel.HIGH, true, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to promote."),
                                requiredString("groupId", "Group id.")),
                        invocation -> operation(admin.groups().promoteSubadmin(stringArg(invocation, "userId"), stringArg(invocation, "groupId")))),
                tool("nextcloud.admin.subadmins.demote", "Demote a user from subadmin for a group.", AdminRiskLevel.HIGH, true, Set.of(ADMIN_WRITE),
                        params(requiredString("userId", "User id to demote."),
                                requiredString("groupId", "Group id.")),
                        invocation -> operation(admin.groups().demoteSubadmin(stringArg(invocation, "userId"), stringArg(invocation, "groupId")))),
                tool("nextcloud.admin.apps.list", "List installed apps.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ, ADMIN_APPS),
                        ToolInputSchema.empty(),
                        invocation -> ToolResult.ok(Map.of("apps", admin.apps().listApps()))),
                tool("nextcloud.admin.apps.enabled", "List enabled apps.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ, ADMIN_APPS),
                        ToolInputSchema.empty(),
                        invocation -> ToolResult.ok(Map.of("apps", admin.apps().listEnabledApps()))),
                tool("nextcloud.admin.apps.disabled", "List disabled apps.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ, ADMIN_APPS),
                        ToolInputSchema.empty(),
                        invocation -> ToolResult.ok(Map.of("apps", admin.apps().listDisabledApps()))),
                tool("nextcloud.admin.apps.get", "Read app information.", AdminRiskLevel.LOW, false, Set.of(ADMIN_READ, ADMIN_APPS),
                        params(requiredString("appId", "App id to inspect.")),
                        invocation -> ToolResult.ok(appMap(admin.apps().getAppInfo(stringArg(invocation, "appId"))))),
                tool("nextcloud.admin.apps.enable", "Enable an app.", AdminRiskLevel.CRITICAL, true, Set.of(ADMIN_WRITE, ADMIN_APPS),
                        params(requiredString("appId", "App id to enable.")),
                        invocation -> appOperation(admin.apps().enableApp(stringArg(invocation, "appId")))),
                tool("nextcloud.admin.apps.disable", "Disable an app.", AdminRiskLevel.CRITICAL, true, Set.of(ADMIN_WRITE, ADMIN_APPS),
                        params(requiredString("appId", "App id to disable.")),
                        invocation -> appOperation(admin.apps().disableApp(stringArg(invocation, "appId")))),
                occTool("nextcloud.admin.occ.maintenance_mode", "Build an OCC maintenance-mode command plan.",
                        params(requiredBoolean("enabled", "Desired maintenance mode state.")),
                        invocation -> occPlan(occ.maintenanceMode(booleanArg(invocation, "enabled")))),
                occTool("nextcloud.admin.occ.files_scan", "Build an OCC files-scan command plan.",
                        params(requiredString("userId", "User id to scan.")),
                        invocation -> occPlan(occ.filesScan(stringArg(invocation, "userId")))),
                occTool("nextcloud.admin.occ.config_get", "Build an OCC config-get command plan.",
                        params(requiredString("app", "Config app."),
                                requiredString("key", "Config key.")),
                        invocation -> occPlan(occ.configGet(stringArg(invocation, "app"), stringArg(invocation, "key")))),
                occTool("nextcloud.admin.occ.config_set", "Build an OCC config-set command plan.",
                        params(requiredString("app", "Config app."),
                                requiredString("key", "Config key."),
                                requiredString("value", "Config value.")),
                        invocation -> occPlan(occ.configSet(stringArg(invocation, "app"), stringArg(invocation, "key"), stringArg(invocation, "value")))),
                occTool("nextcloud.admin.occ.background_jobs", "Build an OCC background job list command plan.",
                        ToolInputSchema.empty(),
                        invocation -> occPlan(occ.backgroundJobList())),
                occTool("nextcloud.admin.occ.recover_admin_group", "Build an OCC admin-group recovery command plan.",
                        params(requiredString("userId", "User id to add to admin group.")),
                        invocation -> occPlan(occ.recoverAdminGroup(stringArg(invocation, "userId")))));
    }

    private static ToolRegistration tool(
            String name,
            String description,
            AdminRiskLevel riskLevel,
            boolean destructive,
            Set<String> scopes,
            ToolInputSchema inputSchema,
            ToolHandlerFunction handler) {
        ToolDescriptor descriptor = new ToolDescriptor(
                new ToolId(name),
                name,
                description,
                inputSchema,
                ToolOutputSchema.object(),
                new ToolSecurity(scopes, destructive),
                metadata("admin", riskLevel, destructive));
        return new ToolRegistration(descriptor, handler::invoke);
    }

    private static ToolRegistration occTool(String name, String description, ToolInputSchema inputSchema, ToolHandlerFunction handler) {
        ToolDescriptor descriptor = new ToolDescriptor(
                new ToolId(name),
                name,
                description,
                inputSchema,
                ToolOutputSchema.object(),
                new ToolSecurity(Set.of(ADMIN_OCC), true),
                metadata("admin-occ", AdminRiskLevel.CRITICAL, true));
        return new ToolRegistration(descriptor, handler::invoke);
    }

    private static Map<String, Object> metadata(String category, AdminRiskLevel riskLevel, boolean destructive) {
        return Map.of(
                "category", category,
                "risk", riskLevel.name().toLowerCase(),
                "confirmationRequired", destructive || riskLevel == AdminRiskLevel.HIGH || riskLevel == AdminRiskLevel.CRITICAL);
    }

    private static ToolResult operation(AdminProvisioningOperation operation) {
        return ToolResult.ok(Map.of(
                "endpoint", operation.endpoint(),
                "statusCode", operation.statusCode()));
    }

    private static ToolResult appOperation(AdminAppOperation operation) {
        return ToolResult.ok(Map.of(
                "endpoint", operation.endpoint(),
                "statusCode", operation.statusCode(),
                "risk", operation.riskLevel().name().toLowerCase()));
    }

    private static ToolResult occPlan(AdminOccCommandPlan plan) {
        return ToolResult.ok(Map.of(
                "operation", plan.operation(),
                "command", plan.command(),
                "risk", plan.riskLevel().name().toLowerCase(),
                "executes", false));
    }

    private static AdminUserCreateRequest createUserRequest(ToolInvocation invocation) {
        return new AdminUserCreateRequest(
                stringArg(invocation, "userId"),
                stringArg(invocation, "password"),
                optionalStringArg(invocation, "displayName"),
                optionalStringArg(invocation, "email"),
                stringListArg(invocation, "groups"),
                stringListArg(invocation, "subadmins"),
                optionalStringArg(invocation, "quota"),
                null);
    }

    private static Map<String, Object> userMap(AdminUser user) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "id", user.id());
        put(values, "displayName", user.displayName());
        put(values, "email", user.email());
        values.put("enabled", user.enabled());
        put(values, "quota", user.quota());
        return Map.copyOf(values);
    }

    private static Map<String, Object> groupMap(AdminGroup group) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "id", group.id());
        put(values, "displayName", group.displayName());
        return Map.copyOf(values);
    }

    private static Map<String, Object> appMap(AdminApp app) {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "id", app.id());
        put(values, "name", app.name());
        put(values, "version", app.version());
        put(values, "enabled", app.enabled());
        return Map.copyOf(values);
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

    private static ToolParameter optionalString(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.STRING, description);
    }

    private static ToolParameter optionalInteger(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.INTEGER, description);
    }

    private static ToolParameter optionalArray(String name, String description) {
        return ToolParameter.optional(name, ToolValueType.ARRAY, description);
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

    private static boolean booleanArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value instanceof Boolean bool) {
            return bool;
        }
        throw new IllegalArgumentException(name + " must be a boolean");
    }

    private static List<String> stringListArg(ToolInvocation invocation, String name) {
        Object value = invocation.arguments().get(name);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .filter(text -> !text.isBlank())
                    .toList();
        }
        throw new IllegalArgumentException(name + " must be an array of strings");
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
