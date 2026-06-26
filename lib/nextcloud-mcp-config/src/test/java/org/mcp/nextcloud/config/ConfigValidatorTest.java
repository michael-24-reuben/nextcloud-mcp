package org.mcp.nextcloud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.config.validation.ConfigValidationError;
import org.mcp.nextcloud.config.validation.ConfigValidator;

class ConfigValidatorTest {
    @Test
    void validatesRequiredAccountFieldsWithoutLeakingSecrets() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "main", "not-absolute", "", "", true, false, true, List.of("nextcloud.files.read"));
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(), Map.of("main", account), new ToolCatalogConfig(Map.of()), NextcloudAdminConfig.disabled());

        List<ConfigValidationError> errors = new ConfigValidator().validate(config);

        assertEquals(3, errors.size());
        assertTrue(errors.stream().anyMatch(error -> error.path().equals("accounts.main.baseUrl")));
        assertTrue(errors.stream().anyMatch(error -> error.path().equals("accounts.main.username")));
        assertTrue(errors.stream().anyMatch(error -> error.path().equals("accounts.main.appPassword")));
    }

    @Test
    void validBasicAccountPasses() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "main", "https://cloud.example.com", "michael", "${NC_APP_PASSWORD}", true, false, true, List.of("nextcloud.files.read"));
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(), Map.of("main", account), new ToolCatalogConfig(Map.of()), NextcloudAdminConfig.disabled());

        assertTrue(new ConfigValidator().validate(config).isEmpty());
    }

    @Test
    void validatesEnabledAdminConfigReferencesAdminAccount() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "main", "https://cloud.example.com", "temporary", "${NC_APP_PASSWORD}", true, false, true, List.of());
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(), Map.of("main", account), new ToolCatalogConfig(Map.of()), new NextcloudAdminConfig(true, "main"));

        List<ConfigValidationError> errors = new ConfigValidator().validate(config);

        assertEquals(1, errors.size());
        assertEquals("admin.accountId", errors.getFirst().path());
        assertEquals("admin account must be marked admin", errors.getFirst().message());
    }

    @Test
    void acceptsEnabledAdminConfigForEnabledAdminAccount() {
        NextcloudAccountConfig account = new NextcloudAccountConfig(
                "admin", "https://cloud.example.com", "admin", "${NC_ADMIN_APP_PASSWORD}", false, true, true, List.of("nextcloud.admin"));
        NextcloudMcpConfig config = new NextcloudMcpConfig(
                ServerConfig.defaults(), Map.of("admin", account), new ToolCatalogConfig(Map.of()), new NextcloudAdminConfig(true, "admin"));

        assertTrue(new ConfigValidator().validate(config).isEmpty());
    }
}
