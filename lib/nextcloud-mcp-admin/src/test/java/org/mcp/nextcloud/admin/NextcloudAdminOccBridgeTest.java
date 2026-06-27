package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class NextcloudAdminOccBridgeTest {
    @Test
    void buildsAllowlistedAioOccCommandsWithoutExecutingThem() {
        NextcloudAdminOccBridge bridge = new NextcloudAdminOccBridge("nextcloud-aio-nextcloud");

        AdminOccCommandPlan maintenance = bridge.maintenanceMode(true);
        AdminOccCommandPlan configSet = bridge.configSet("files", "max_chunk_size", "10485760");
        AdminOccCommandPlan recovery = bridge.recoverAdminGroup("temporary");

        assertEquals(AdminRiskLevel.CRITICAL, maintenance.riskLevel());
        assertEquals(List.of("docker", "exec", "-u", "www-data", "nextcloud-aio-nextcloud", "php", "occ", "maintenance:mode", "--on"),
                maintenance.command());
        assertEquals(List.of("docker", "exec", "-u", "www-data", "nextcloud-aio-nextcloud", "php", "occ", "config:app:set", "files", "max_chunk_size", "--value", "10485760"),
                configSet.command());
        assertEquals(List.of("docker", "exec", "-u", "www-data", "nextcloud-aio-nextcloud", "php", "occ", "group:adduser", "admin", "temporary"),
                recovery.command());
    }
}
