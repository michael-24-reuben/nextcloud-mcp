package org.mcp.nextcloud.admin;

import java.util.ArrayList;
import java.util.List;

import org.mcp.nextcloud.core.util.Preconditions;

public final class NextcloudAdminOccBridge {
    private static final String DEFAULT_CONTAINER = "nextcloud-aio-nextcloud";

    private final String containerName;

    public NextcloudAdminOccBridge() {
        this(DEFAULT_CONTAINER);
    }

    public NextcloudAdminOccBridge(String containerName) {
        this.containerName = Preconditions.requireNonBlank(containerName, "container name");
    }

    public AdminOccCommandPlan maintenanceMode(boolean enabled) {
        return occ("maintenance_mode", "maintenance:mode", "--" + (enabled ? "on" : "off"));
    }

    public AdminOccCommandPlan filesScan(String userId) {
        return occ("files_scan", "files:scan", Preconditions.requireNonBlank(userId, "user id"));
    }

    public AdminOccCommandPlan configGet(String app, String key) {
        return occ("config_get", "config:app:get",
                Preconditions.requireNonBlank(app, "app"),
                Preconditions.requireNonBlank(key, "key"));
    }

    public AdminOccCommandPlan configSet(String app, String key, String value) {
        return occ("config_set", "config:app:set",
                Preconditions.requireNonBlank(app, "app"),
                Preconditions.requireNonBlank(key, "key"),
                "--value",
                Preconditions.requireNonBlank(value, "value"));
    }

    public AdminOccCommandPlan backgroundJobList() {
        return occ("background_job_list", "background-job:list");
    }

    public AdminOccCommandPlan recoverAdminGroup(String userId) {
        return occ("recover_admin_group", "group:adduser", "admin", Preconditions.requireNonBlank(userId, "user id"));
    }

    private AdminOccCommandPlan occ(String operation, String... args) {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("exec");
        command.add("-u");
        command.add("www-data");
        command.add(containerName);
        command.add("php");
        command.add("occ");
        command.addAll(List.of(args));
        return new AdminOccCommandPlan(operation, command, AdminRiskLevel.CRITICAL);
    }
}
