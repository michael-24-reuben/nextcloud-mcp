package org.mcp.nextcloud.server;

import java.util.List;

record AccountCreateRequest(
        String accountId,
        String nextcloudAccountId,
        String baseUrl,
        String username,
        String appPassword,
        String displayName,
        String email,
        Boolean defaultAccount,
        Boolean admin,
        Boolean enabled,
        List<String> scopes) {
}

record AccountPatchRequest(
        String accountId,
        String nextcloudAccountId,
        String baseUrl,
        String username,
        String displayName,
        String email,
        Boolean defaultAccount,
        Boolean admin,
        Boolean enabled,
        List<String> scopes) {
}

record AppPasswordRequest(String appPassword) {
}

record AdminUserCreateRouteRequest(
        String userId,
        String password,
        String displayName,
        String email,
        List<String> groups,
        List<String> subadmins,
        String quota,
        String language) {
}

record AdminUserFieldUpdateRequest(String key, String value) {
}

record AdminGroupCreateRequest(String groupId) {
}

record AdminGroupPatchRequest(String displayName) {
}

record AdminGroupMembershipRequest(String groupId) {
}

record OccMaintenanceModeRequest(Boolean enabled) {
}

record OccFilesScanRequest(String userId) {
}

record OccConfigGetRequest(String app, String key) {
}

record OccConfigSetRequest(String app, String key, String value) {
}

record OccRecoverAdminGroupRequest(String userId) {
}
