package org.mcp.nextcloud.client;

public record ShareUpdateRequest(
        Integer permissions,
        String password,
        Boolean publicUpload,
        String expireDate,
        String note,
        String label,
        String attributes,
        Boolean sendMail) {
}
