package org.mcp.nextcloud.admin;

public record AdminAppOperation(String endpoint, int statusCode, AdminRiskLevel riskLevel) {
}
