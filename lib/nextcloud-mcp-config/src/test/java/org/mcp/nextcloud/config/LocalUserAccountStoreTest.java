package org.mcp.nextcloud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalUserAccountStoreTest {
    @TempDir
    Path tempDir;

    @Test
    void yamlLoaderMergesUserEnvRecordsIntoAccounts() throws Exception {
        Path config = tempDir.resolve("server.yaml");
        Path users = tempDir.resolve("db").resolve("u");
        Files.createDirectories(users);
        Files.writeString(config, """
                server:
                  enabled: true
                  host: 127.0.0.1
                  port: 8080
                admin:
                  enabled: false
                """, StandardCharsets.UTF_8);
        Files.writeString(users.resolve("usr-temporary.env"), """
                ACCOUNT_KEY=temporary
                ACCOUNT_NAME=temporary
                BASE_URL=https://cloud.example.com
                APP_PASSWORD=local-app-password
                DEFAULT_ACCOUNT=true
                ADMIN=false
                ENABLED=true
                SCOPES=nextcloud.files.read,nextcloud.files.write
                """, StandardCharsets.UTF_8);

        NextcloudMcpConfig loaded = new YamlConfigLoader().load(config);

        assertEquals(1, loaded.accounts().size());
        NextcloudAccountConfig account = loaded.accounts().get("temporary");
        assertEquals("temporary", account.id());
        assertEquals("https://cloud.example.com", account.baseUrl());
        assertEquals("temporary", account.username());
        assertEquals("local-app-password", account.appPassword());
        assertTrue(account.defaultAccount());
        assertFalse(account.admin());
        assertTrue(account.enabled());
        assertEquals(2, account.scopes().size());
        assertFalse(loaded.admin().enabled());
    }

    @Test
    void firstEnabledAdminUserEnablesAdminConfigWhenYamlDoesNotSelectOne() throws Exception {
        Path config = tempDir.resolve("server.yaml");
        Path users = tempDir.resolve("db").resolve("u");
        Files.createDirectories(users);
        Files.writeString(config, """
                server:
                  enabled: true
                  host: 127.0.0.1
                  port: 8080
                admin:
                  enabled: false
                """, StandardCharsets.UTF_8);
        Files.writeString(users.resolve("usr-admin.env"), """
                ACCOUNT_NAME=admin
                BASE_URL=https://cloud.example.com
                APP_PASSWORD=local-admin-password
                ADMIN=true
                ENABLED=true
                SCOPES=nextcloud.admin.users.read
                """, StandardCharsets.UTF_8);

        NextcloudMcpConfig loaded = new YamlConfigLoader().load(config);

        assertTrue(loaded.admin().enabled());
        assertEquals("admin", loaded.admin().accountId());
    }
}
