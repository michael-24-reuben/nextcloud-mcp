package org.mcp.nextcloud.config.validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.mcp.nextcloud.config.NextcloudAccountConfig;
import org.mcp.nextcloud.config.NextcloudAdminConfig;
import org.mcp.nextcloud.config.NextcloudMcpConfig;

public final class ConfigValidator {
    public List<ConfigValidationError> validate(NextcloudMcpConfig config) {
        List<ConfigValidationError> errors = new ArrayList<>();
        if (config == null) {
            return List.of(new ConfigValidationError("$", "config is required"));
        }
        config.accounts().forEach((id, account) -> validateAccount(id, account, errors));
        validateAdmin(config.admin(), config, errors);
        return List.copyOf(errors);
    }

    private static void validateAccount(String id, NextcloudAccountConfig account, List<ConfigValidationError> errors) {
        String path = "accounts." + id;
        if (account == null) {
            errors.add(new ConfigValidationError(path, "account is required"));
            return;
        }
        require(path + ".baseUrl", account.baseUrl(), errors);
        if (account.baseUrl() != null && !account.baseUrl().isBlank()) {
            try {
                URI uri = URI.create(account.baseUrl());
                if (uri.getScheme() == null || uri.getHost() == null) {
                    errors.add(new ConfigValidationError(path + ".baseUrl", "baseUrl must be absolute"));
                }
            } catch (IllegalArgumentException ex) {
                errors.add(new ConfigValidationError(path + ".baseUrl", "baseUrl is invalid"));
            }
        }
        require(path + ".username", account.username(), errors);
        require(path + ".appPassword", account.appPassword(), errors);
        if (account.admin() && !account.enabled()) {
            errors.add(new ConfigValidationError(path + ".enabled", "admin accounts must be explicitly enabled before use"));
        }
    }

    private static void validateAdmin(NextcloudAdminConfig admin, NextcloudMcpConfig config, List<ConfigValidationError> errors) {
        if (admin == null || !admin.enabled()) {
            return;
        }
        if (admin.accountId() == null || admin.accountId().isBlank()) {
            errors.add(new ConfigValidationError("admin.accountId", "value is required when admin API is enabled"));
            return;
        }
        NextcloudAccountConfig account = config.accounts().get(admin.accountId());
        if (account == null) {
            errors.add(new ConfigValidationError("admin.accountId", "admin account must reference a configured account"));
            return;
        }
        if (!account.enabled()) {
            errors.add(new ConfigValidationError("admin.accountId", "admin account must be enabled"));
        }
        if (!account.admin()) {
            errors.add(new ConfigValidationError("admin.accountId", "admin account must be marked admin"));
        }
    }

    private static void require(String path, String value, List<ConfigValidationError> errors) {
        if (value == null || value.isBlank()) {
            errors.add(new ConfigValidationError(path, "value is required"));
        }
    }
}
