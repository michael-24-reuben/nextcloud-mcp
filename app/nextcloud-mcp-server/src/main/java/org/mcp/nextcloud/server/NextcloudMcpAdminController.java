package org.mcp.nextcloud.server;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mcp.nextcloud.admin.AdminApp;
import org.mcp.nextcloud.admin.AdminAppOperation;
import org.mcp.nextcloud.admin.AdminGroup;
import org.mcp.nextcloud.admin.AdminOccCommandPlan;
import org.mcp.nextcloud.admin.AdminProvisioningOperation;
import org.mcp.nextcloud.admin.AdminUser;
import org.mcp.nextcloud.admin.AdminUserCreateRequest;
import org.mcp.nextcloud.admin.NextcloudAdminClient;
import org.mcp.nextcloud.admin.NextcloudAdminOccBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NextcloudMcpAdminController {
    private final NextcloudMcpRuntimeService runtimeService;
    private final NextcloudAdminOccBridge occBridge;

    @Autowired
    public NextcloudMcpAdminController(NextcloudMcpRuntimeService runtimeService) {
        this(runtimeService, new NextcloudAdminOccBridge());
    }

    NextcloudMcpAdminController(NextcloudMcpRuntimeService runtimeService, NextcloudAdminOccBridge occBridge) {
        this.runtimeService = runtimeService;
        this.occBridge = occBridge;
    }

    @GetMapping("/api/v1/admin/users")
    Map<String, Object> users(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset) {
        return Map.of("users", admin().users().listUsers(search, limit, offset));
    }

    @PostMapping("/api/v1/admin/users")
    Map<String, Object> createUser(@RequestBody AdminUserCreateRouteRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "user create request is required");
        }
        String userId = admin().users().createUser(new AdminUserCreateRequest(
                request.userId(),
                request.password(),
                request.displayName(),
                request.email(),
                request.groups(),
                request.subadmins(),
                request.quota(),
                request.language())).id();
        return Map.of("userId", userId);
    }

    @GetMapping("/api/v1/admin/users/{userId}")
    Map<String, Object> user(@PathVariable String userId) {
        return userMap(admin().users().getUser(userId));
    }

    @PatchMapping("/api/v1/admin/users/{userId}")
    Map<String, Object> patchUser(@PathVariable String userId, @RequestBody AdminUserFieldUpdateRequest request) {
        return updateUserField(userId, request);
    }

    @PutMapping("/api/v1/admin/users/{userId}")
    Map<String, Object> putUser(@PathVariable String userId, @RequestBody AdminUserFieldUpdateRequest request) {
        return updateUserField(userId, request);
    }

    @DeleteMapping("/api/v1/admin/users/{userId}")
    Map<String, Object> deleteUser(@PathVariable String userId) {
        return operation(admin().users().deleteUser(userId));
    }

    @PutMapping("/api/v1/admin/users/{userId}/disable")
    Map<String, Object> disableUser(@PathVariable String userId) {
        return operation(admin().users().disableUser(userId));
    }

    @PutMapping("/api/v1/admin/users/{userId}/enable")
    Map<String, Object> enableUser(@PathVariable String userId) {
        return operation(admin().users().enableUser(userId));
    }

    @PostMapping("/api/v1/admin/users/{userId}/welcome")
    Map<String, Object> resendWelcomeEmail(@PathVariable String userId) {
        return operation(admin().users().resendWelcomeEmail(userId));
    }

    @GetMapping("/api/v1/admin/users/{userId}/groups")
    Map<String, Object> userGroups(@PathVariable String userId) {
        return Map.of("groups", admin().users().getUserGroups(userId));
    }

    @PostMapping("/api/v1/admin/users/{userId}/groups")
    Map<String, Object> addUserToGroup(@PathVariable String userId, @RequestBody AdminGroupMembershipRequest request) {
        return operation(admin().groups().addUserToGroup(userId, requiredGroupId(request)));
    }

    @DeleteMapping("/api/v1/admin/users/{userId}/groups/{groupId}")
    Map<String, Object> removeUserFromGroup(@PathVariable String userId, @PathVariable String groupId) {
        return operation(admin().groups().removeUserFromGroup(userId, groupId));
    }

    @GetMapping("/api/v1/admin/users/{userId}/subadmins")
    Map<String, Object> userSubadmins(@PathVariable String userId) {
        return Map.of("subadmins", admin().users().getSubadminGroups(userId));
    }

    @PostMapping("/api/v1/admin/users/{userId}/subadmins")
    Map<String, Object> promoteSubadmin(@PathVariable String userId, @RequestBody AdminGroupMembershipRequest request) {
        return operation(admin().groups().promoteSubadmin(userId, requiredGroupId(request)));
    }

    @DeleteMapping("/api/v1/admin/users/{userId}/subadmins/{groupId}")
    Map<String, Object> demoteSubadmin(@PathVariable String userId, @PathVariable String groupId) {
        return operation(admin().groups().demoteSubadmin(userId, groupId));
    }

    @GetMapping("/api/v1/admin/groups")
    Map<String, Object> groups(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset) {
        return Map.of("groups", admin().groups().listGroups(search, limit, offset));
    }

    @PostMapping("/api/v1/admin/groups")
    Map<String, Object> createGroup(@RequestBody AdminGroupCreateRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "group create request is required");
        }
        return groupMap(admin().groups().createGroup(request.groupId()));
    }

    @GetMapping("/api/v1/admin/groups/{groupId}")
    Map<String, Object> group(@PathVariable String groupId) {
        return Map.of("users", admin().groups().getGroupMembers(groupId));
    }

    @PatchMapping("/api/v1/admin/groups/{groupId}")
    Map<String, Object> patchGroup(@PathVariable String groupId, @RequestBody AdminGroupPatchRequest request) {
        return updateGroupDisplayName(groupId, request);
    }

    @PutMapping("/api/v1/admin/groups/{groupId}")
    Map<String, Object> putGroup(@PathVariable String groupId, @RequestBody AdminGroupPatchRequest request) {
        return updateGroupDisplayName(groupId, request);
    }

    @DeleteMapping("/api/v1/admin/groups/{groupId}")
    Map<String, Object> deleteGroup(@PathVariable String groupId) {
        return operation(admin().groups().deleteGroup(groupId));
    }

    @GetMapping("/api/v1/admin/groups/{groupId}/subadmins")
    Map<String, Object> groupSubadmins(@PathVariable String groupId) {
        return Map.of("subadmins", admin().groups().getGroupSubadmins(groupId));
    }

    @GetMapping("/api/v1/admin/apps")
    Map<String, Object> apps() {
        return Map.of("apps", admin().apps().listApps());
    }

    @GetMapping("/api/v1/admin/apps/enabled")
    Map<String, Object> enabledApps() {
        return Map.of("apps", admin().apps().listEnabledApps());
    }

    @GetMapping("/api/v1/admin/apps/disabled")
    Map<String, Object> disabledApps() {
        return Map.of("apps", admin().apps().listDisabledApps());
    }

    @GetMapping("/api/v1/admin/apps/{appId}")
    Map<String, Object> app(@PathVariable String appId) {
        return appMap(admin().apps().getAppInfo(appId));
    }

    @PostMapping("/api/v1/admin/apps/{appId}/enable")
    Map<String, Object> enableApp(@PathVariable String appId) {
        return appOperation(admin().apps().enableApp(appId));
    }

    @PostMapping("/api/v1/admin/apps/{appId}/disable")
    Map<String, Object> disableApp(@PathVariable String appId) {
        return appOperation(admin().apps().disableApp(appId));
    }

    @PostMapping("/api/v1/admin/occ/maintenance-mode")
    Map<String, Object> maintenanceMode(@RequestBody OccMaintenanceModeRequest request) {
        if (request == null || request.enabled() == null) {
            throw new ServerRequestException("request.invalid", "enabled is required");
        }
        return occPlan(occBridge.maintenanceMode(request.enabled()));
    }

    @PostMapping("/api/v1/admin/occ/files-scan")
    Map<String, Object> filesScan(@RequestBody OccFilesScanRequest request) {
        return occPlan(occBridge.filesScan(required(request == null ? null : request.userId(), "userId")));
    }

    @PostMapping("/api/v1/admin/occ/config-get")
    Map<String, Object> configGet(@RequestBody OccConfigGetRequest request) {
        return occPlan(occBridge.configGet(
                required(request == null ? null : request.app(), "app"),
                required(request == null ? null : request.key(), "key")));
    }

    @PostMapping("/api/v1/admin/occ/config-set")
    Map<String, Object> configSet(@RequestBody OccConfigSetRequest request) {
        return occPlan(occBridge.configSet(
                required(request == null ? null : request.app(), "app"),
                required(request == null ? null : request.key(), "key"),
                required(request == null ? null : request.value(), "value")));
    }

    @PostMapping("/api/v1/admin/occ/background-jobs")
    Map<String, Object> backgroundJobs() {
        return occPlan(occBridge.backgroundJobList());
    }

    @PostMapping("/api/v1/admin/occ/recover-admin-group")
    Map<String, Object> recoverAdminGroup(@RequestBody OccRecoverAdminGroupRequest request) {
        return occPlan(occBridge.recoverAdminGroup(required(request == null ? null : request.userId(), "userId")));
    }

    private NextcloudAdminClient admin() {
        return runtimeService.adminClient();
    }

    private Map<String, Object> updateUserField(String userId, AdminUserFieldUpdateRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "user field update request is required");
        }
        return operation(admin().users().updateUserField(userId, request.key(), request.value()));
    }

    private Map<String, Object> updateGroupDisplayName(String groupId, AdminGroupPatchRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "group patch request is required");
        }
        return operation(admin().groups().updateGroupDisplayName(groupId, request.displayName()));
    }

    private static String requiredGroupId(AdminGroupMembershipRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "groupId is required");
        }
        return required(request.groupId(), "groupId");
    }

    private static Map<String, Object> operation(AdminProvisioningOperation operation) {
        return Map.of("endpoint", operation.endpoint(), "statusCode", operation.statusCode());
    }

    private static Map<String, Object> appOperation(AdminAppOperation operation) {
        return Map.of(
                "endpoint", operation.endpoint(),
                "statusCode", operation.statusCode(),
                "risk", operation.riskLevel().name().toLowerCase());
    }

    private static Map<String, Object> occPlan(AdminOccCommandPlan plan) {
        return Map.of(
                "operation", plan.operation(),
                "command", plan.command(),
                "risk", plan.riskLevel().name().toLowerCase(),
                "executes", false);
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

    private static void put(Map<String, Object> values, String name, Object value) {
        if (value != null) {
            values.put(name, value);
        }
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new ServerRequestException("request.invalid", name + " is required");
        }
        return value;
    }
}
