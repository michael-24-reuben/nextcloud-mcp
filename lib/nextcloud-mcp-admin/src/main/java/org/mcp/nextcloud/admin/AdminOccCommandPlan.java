package org.mcp.nextcloud.admin;

import java.util.List;

public record AdminOccCommandPlan(String operation, List<String> command, AdminRiskLevel riskLevel) {
    public AdminOccCommandPlan {
        command = List.copyOf(command);
    }
}
