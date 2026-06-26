package org.mcp.nextcloud.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record AdminUserCreateRequest(
        String userId,
        String password,
        String displayName,
        String email,
        List<String> groups,
        List<String> subadmins,
        String quota,
        String language) {
    public AdminUserCreateRequest {
        userId = Preconditions.requireNonBlank(userId, "user id");
        password = Preconditions.requireNonBlank(password, "password");
        groups = groups == null ? List.of() : List.copyOf(groups);
        subadmins = subadmins == null ? List.of() : List.copyOf(subadmins);
    }

    public static AdminUserCreateRequest withPassword(String userId, String password) {
        return new AdminUserCreateRequest(userId, password, null, null, List.of(), List.of(), null, null);
    }

    List<Map.Entry<String, String>> toFormFields() {
        List<Map.Entry<String, String>> fields = new ArrayList<>();
        fields.add(Map.entry("userid", userId));
        fields.add(Map.entry("password", password));
        addIfPresent(fields, "displayName", displayName);
        addIfPresent(fields, "email", email);
        groups.stream()
                .filter(value -> value != null && !value.isBlank())
                .forEach(value -> fields.add(Map.entry("groups[]", value)));
        subadmins.stream()
                .filter(value -> value != null && !value.isBlank())
                .forEach(value -> fields.add(Map.entry("subadmin[]", value)));
        addIfPresent(fields, "quota", quota);
        addIfPresent(fields, "language", language);
        return List.copyOf(fields);
    }

    private static void addIfPresent(List<Map.Entry<String, String>> fields, String key, String value) {
        if (value != null && !value.isBlank()) {
            fields.add(Map.entry(key, value));
        }
    }
}
